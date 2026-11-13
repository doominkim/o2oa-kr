package com.x.message.assemble.communicate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.SSLHttpClientUtil;
import com.x.base.core.project.tools.StringTools;
import com.x.message.assemble.communicate.message.AndFxMessage;
import com.x.message.core.entity.Message;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class AndFxConsumeQueue extends AbstractQueue<Message> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AndFxConsumeQueue.class);

	private static final Gson gson = new Gson();

	@Override
	protected void execute(Message message) throws Exception {

		if (Config.andFx().getEnable() && Config.andFx().getMessageEnable()) {
			AndFxMessage m = new AndFxMessage();
			Person person = this.getPerson(message.getPerson());
			m.setReceiver(new String[]{person.getMobile()});
			m.setAppKey(Config.andFx().getMsgAppKey());
			m.setClient_id(Config.andFx().getClientId());
			m.setSender(Config.andFx().getMsgSender());
			m.setUuid(StringUtils.replace(StringTools.uniqueToken(), "-", ""));

			String andFxUrl = null;
			if (needTransferLink(message.getType())) {
				String openUrl = getOpenUrl(message);
				if (StringUtils.isNotEmpty(openUrl)) {
					Map<String, String> map = new HashMap<>();
					map.put("url", openUrl);
					andFxUrl = "native://openurl?data=" + gson.toJson(map);
					LOGGER.debug("모바일 오피스 메시지 열기 url:{}", andFxUrl);
				}
			}

			String cardTitle;
			if (MessageConnector.TYPE_CMS_PUBLISH.equals(message.getType())
					|| MessageConnector.TYPE_CMS_PUBLISH_TO_CREATOR.equals(message.getType())) {
				String categoryName = AndFxConsumeQueue.OuterMessageHelper.getPropertiesFromBody("categoryName", message.getBody());
				if (StringUtils.isEmpty(categoryName)) {
					categoryName = "게시판 알림";
				}
				cardTitle = "【"+categoryName+"】";
			} else {
				String processName = AndFxConsumeQueue.OuterMessageHelper.getPropertiesFromBody("processName", message.getBody());
				if (StringUtils.isEmpty(processName)) {
					processName = "결재 알림";
				}
				cardTitle = "【"+processName+"】";
			}
			m.setContent(gson.toJson(AndFxMessage.getContentInst(Config.andFx().getMsgBoxTitle(), cardTitle, message.getTitle(), andFxUrl)));
			m.setSign(this.sign(m));
			String address = Config.andFx().getMsgApi();
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("모바일 오피스 메시지 본문:{}", m::toString);
			}
			AndFxMessageResp resp;
			if(address.startsWith("https://test.yd.cmzq-office.com")){
				String sslPassword = "u24Ufe4%.ca@,{]";
				File file = new File(Config.dir_config(), "client.p12");
				SSLHttpClientUtil httpClient = new SSLHttpClientUtil(sslPassword, file.getAbsolutePath());
				String res = httpClient.post(address, m.toString(), null);
				resp = gson.fromJson(res, AndFxMessageResp.class);
			}else {
				LOGGER.info("모바일 오피스 메시지 본문:{}", m::toString);
				resp = HttpConnection.postAsObject(address, null, m.toString(),
						AndFxMessageResp.class);
			}
			if (resp.getCode() != 0) {
				ExceptionAndFxMessage e = new ExceptionAndFxMessage(resp.getCode(), resp.getMsg());
				LOGGER.error(e);
			} else {
				success(message.getId());
			}
		}
	}

	private String sign(AndFxMessage message) throws Exception{
		StringBuffer buffer = new StringBuffer();
		buffer.append("appKey=" + message.getAppKey())
				.append("client_id=" + message.getClient_id())
				.append("sender=" + message.getSender())
				.append("uuid=" + message.getUuid());
		return DigestUtils.sha1Hex(buffer.toString() + Config.andFx().getMsgAppSecret());
	}

	/**
	 * 열기 주소 판단
	 *
	 * @param message
	 * @return
	 */
	private String getOpenUrl(Message message) {
		String openUrl = "";
		// cms 문서
		if (MessageConnector.TYPE_CMS_PUBLISH.equals(message.getType())
				|| MessageConnector.TYPE_CMS_PUBLISH_TO_CREATOR.equals(message.getType())) {
			openUrl = getOpenCMSDocumentUrl(message.getBody());
		} else { // 프로세스 업무 문서 관련
			openUrl = getOpenWorkUrl(message.getBody());
		}
		return openUrl;
	}

	private Person getPerson(String person) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			return business.organization().person().getObject(person);
		}
	}

	/**
	 * 메시지 소비 성공 표시
	 *
	 * @param id
	 * @throws Exception
	 */
	private void success(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Message message = emc.find(id, Message.class);
			if (null != message) {
				emc.beginTransaction(Message.class);
				message.setConsumed(true);
				emc.commit();
			}
		}
	}

	/**
	 * 문서를 여는 url
	 *
	 * @param messageBody
	 * @return
	 */
	private String getOpenCMSDocumentUrl(String messageBody) {
		String o2oaUrl = null;
		try {
			o2oaUrl = Config.andFx().getWorkUrl() + "andfxsso.html?redirect=";
			return OuterMessageHelper.getOpenCMSDocumentUrl(o2oaUrl, messageBody);
		} catch (Exception e) {
			LOGGER.error(e);
			return null;
		}
	}

	/**
	 * 업무 문서를 여는 url
	 *
	 * @param messageBody
	 * @return
	 */
	private String getOpenWorkUrl(String messageBody) {
		try {
			String openPage = OuterMessageHelper.getOpenPageUrl(messageBody);
			String o2oaUrl = Config.andFx().getWorkUrl() + "andfxsso.html?redirect=";
			if (StringUtils.isEmpty(openPage)) {
				String work = OuterMessageHelper.getWorkIdFromBody(messageBody);
				if (StringUtils.isEmpty(work) || StringUtils.isEmpty(o2oaUrl)) {
					return null;
				}
				String workUrl = "workmobilewithaction.html?workid=" + work;
				String messageRedirectPortal = Config.andFx().getMessageRedirectPortal();
				if (StringUtils.isNotBlank(messageRedirectPortal)) {
					String portal = "portalmobile.html?id=" + messageRedirectPortal;
					portal = URLEncoder.encode(portal, DefaultCharset.name);
					workUrl += "&redirectlink=" + portal;
				}
				LOGGER.debug("o2oa workUrl：" + workUrl);
				workUrl = URLEncoder.encode(workUrl, DefaultCharset.name);
				o2oaUrl = o2oaUrl + workUrl;
			} else {
				o2oaUrl = o2oaUrl + openPage;
			}
			LOGGER.info("o2oa 주소:" + o2oaUrl);
			o2oaUrl = URLEncoder.encode(o2oaUrl, DefaultCharset.name);
			return o2oaUrl;
		} catch (Exception e) {
			LOGGER.error(e);
		}

		return null;
	}

	/**
	 * 딩톡 메시지를 markdown 형식 메시지로 변환할지 여부. 딩톡 업무 링크 설정 여부, 업무 메시지 여부에 따라 결정(현재 업무 메시지만 지원)
	 *
	 * @param messageType 메시지 유형. 업무 메시지 여부 판단
	 * @return
	 */
	private boolean needTransferLink(String messageType) {
		try {
			String workUrl = Config.andFx().getWorkUrl();
			if (StringUtils.isNotEmpty(workUrl)) {
				return true;
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return false;
	}

	public static class AndFxMessageResp {

		private Integer code;
		private String msg;

		public Integer getCode() {
			return code;
		}

		public void setCode(Integer code) {
			this.code = code;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}
	}

	/**
	 * 공용 메서드
	 */
	public static class OuterMessageHelper {
		/**
		 * 문서를 여는 url
		 * @param messageBody
		 * @return
		 */
		public static String getOpenCMSDocumentUrl(String bizBaseUrl, String messageBody) {
			try {
				String openPage = getOpenPageUrl(messageBody);
				String o2oaUrl = bizBaseUrl;
				if (StringUtils.isEmpty(openPage)) {
					String id = getCmsDocumentId(messageBody);
					if (StringUtils.isEmpty(id)) {
						return null;
					}
					String docUrl = "cmsdocMobile.html?id=" + id;
					docUrl = URLEncoder.encode(docUrl, DefaultCharset.name);
					o2oaUrl = o2oaUrl + docUrl;
				} else {
					o2oaUrl = o2oaUrl + openPage;
				}
				LOGGER.info("o2oa 업무 주소:" + o2oaUrl);
				return o2oaUrl;
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return null;
		}

		/**
		 * body에 openPageUrl 필드가 있는지 여부. 있으면 이 필드를 이동 페이지로 사용
		 *
		 * @param messageBody
		 * @return
		 */
		public static String getOpenPageUrl(String messageBody) {
			try {
				JsonObject object = gson.fromJson(messageBody, JsonObject.class);
				if (object.get("openPageUrl") != null) {
					return object.get("openPageUrl").getAsString();
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return null;
		}

		/**
		 * workid 조회
		 *
		 * @param messageBody
		 * @return
		 */
		public static  String getWorkIdFromBody(String messageBody) {
			try {
				JsonObject object = gson.fromJson(messageBody, JsonObject.class);
				if (object.get("work") != null) {
					return object.get("work").getAsString();
				}
				if (object.get("workCompleted") != null) {
					return object.get("workCompleted").getAsString();
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return "";
		}

		/**
		 * 메시지 본문 내 특정 필드 값 조회
		 * @param propertiesName  예: processName
		 * @param messageBody
		 * @return
		 */
		public static String getPropertiesFromBody(String propertiesName, String messageBody) {
			try {
				JsonObject object = gson.fromJson(messageBody, JsonObject.class);
				if (object.get(propertiesName) != null) {
					return object.get(propertiesName).getAsString();
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return "";
		}

		/**
		 * 이 실행의 전제 조건은 MessageConnector.TYPE_CMS_PUBLISH.equals(message.getType()) ||
		 * MessageConnector.TYPE_CMS_PUBLISH_TO_CREATOR.equals(message.getType()) cms 메시지
		 * body에서 문서 id 조회
		 *
		 * @param messageBody
		 * @return
		 */
		private static String getCmsDocumentId(String messageBody) {
			try {
				JsonObject object = gson.fromJson(messageBody, JsonObject.class);
				if (object.get("id") != null) {
					return object.get("id").getAsString();
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return null;
		}
	}
}
