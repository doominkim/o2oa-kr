package com.x.message.assemble.communicate;

import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

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
import com.x.message.assemble.communicate.message.DingdingMessage;
import com.x.message.core.entity.Message;

public class DingdingConsumeQueue extends AbstractQueue<Message> {

	private static final Logger LOGGER = LoggerFactory.getLogger(DingdingConsumeQueue.class);

	private static final Gson gson = XGsonBuilder.instance();

	protected void execute(Message message) throws Exception {

		if (Config.dingding().getEnable() && Config.dingding().getMessageEnable()) {
			DingdingMessage m = new DingdingMessage();
			Person person = this.getPerson(message.getPerson());
			m.setAgent_id(Long.parseLong(Config.dingding().getAgentId(), 10));
			m.setUserid_list(person.getDingdingId());
			if (StringUtils.isEmpty(m.getUserid_list())) {
				LOGGER.warn("딩톡 메시지를 받을 사용자 없음:{}.", message.getPerson());
				return;
			}
			String openUrl = getOpenUrl(message);
			if (needTransferLink(message.getType()) && StringUtils.isNotEmpty(openUrl)) {
					// dingtalk://dingtalkclient/action/openapp?corpid=로그인면제기업corpId&container_type=work_platform&app_id=0_{앱agentid}&redirect_type=jump&redirect_url=이동url
				String dingtalkUrl = "dingtalk://dingtalkclient/action/openapp?corpid="
						+ Config.dingding().getCorpId() + "&container_type=work_platform&app_id=0_"
						+ Config.dingding().getAgentId() + "&redirect_type=jump&redirect_url="
						+ URLEncoder.encode(openUrl, DefaultCharset.name);
				LOGGER.info("딩톡 pc 메시지 열기 url:{}",  dingtalkUrl);
				// 콘텐츠 관리
				String cardTitle = cardTitle(message.getTitle()); // message.getTitle() 대신 uuid 사용. 메시지 재전송 문제
				if (MessageConnector.TYPE_CMS_PUBLISH.equals(message.getType())
						|| MessageConnector.TYPE_CMS_PUBLISH_TO_CREATOR.equals(message.getType())) {
					String categoryName = DingdingConsumeQueue.OuterMessageHelper.getPropertiesFromBody("categoryName", message.getBody());
					if (StringUtils.isEmpty(categoryName)) {
						categoryName = "게시판 알림";
					}
					m.setActionCardMsg(cardTitle, "# 【"+categoryName+"】 \n " +message.getTitle(), dingtalkUrl);
				} else {
					String processName = DingdingConsumeQueue.OuterMessageHelper.getPropertiesFromBody("processName", message.getBody());
					if (StringUtils.isEmpty(processName)) {
						processName = "결재 알림";
					}

					m.setActionCardMsg(cardTitle, "# 【"+processName+"】 \n " + message.getTitle(), dingtalkUrl);
				}
//					m.getMsg().setMsgtype("markdown");
//					m.getMsg().getMarkdown().setTitle(message.getTitle());
//					m.getMsg().getMarkdown().setText("[" + message.getTitle() + "](" + dingtalkUrl + ")");
			} else {
				m.setTextMsg(message.getTitle());
			}
			String address = Config.dingding().getOapiAddress()
					+ "/topapi/message/corpconversation/asyncsend_v2?access_token="
					+ Config.dingding().corpAccessToken();
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("딩톡 메시지 본문:{}", m::toString);
			}
			DingdingMessageResp resp = HttpConnection.postAsObject(address, null, m.toString(),
					DingdingMessageResp.class);
			if (resp.getErrcode() != 0) {
				ExceptionDingdingMessage e = new ExceptionDingdingMessage(resp.getErrcode(), resp.getErrmsg());
				LOGGER.error(e);
			} else {
				success(message.getId());
			}
		}
	}

	private String cardTitle(String title) {
		String retTitle = title;
		if (StringUtils.isNotEmpty(retTitle)) {
			if (retTitle.length() > 46) {
				retTitle = retTitle.substring(0, 46);
			}
			Random r = new SecureRandom();
			int i = r.nextInt(9999-1000) + 1000;
			retTitle = retTitle + i;
		}
		return retTitle;
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
			openUrl = getDingdingOpenCMSDocumentUrl(message.getBody());
		} else { // 프로세스 업무 문서 관련
			openUrl = getDingdingOpenWorkUrl(message.getBody());
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
	private String getDingdingOpenCMSDocumentUrl(String messageBody) {
		String o2oaUrl = null;
		try {
			o2oaUrl = Config.dingding().getWorkUrl() + "ddsso.html?redirect=";
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
	private String getDingdingOpenWorkUrl(String messageBody) {
		try {
			String openPage = OuterMessageHelper.getOpenPageUrl(messageBody);
			String o2oaUrl = Config.dingding().getWorkUrl() + "ddsso.html?redirect=";
			if (StringUtils.isEmpty(openPage)) {
				String work = OuterMessageHelper.getWorkIdFromBody(messageBody);
				if (StringUtils.isEmpty(work) || StringUtils.isEmpty(o2oaUrl)) {
					return null;
				}
				String workUrl = "workmobilewithaction.html?workid=" + work;
				String messageRedirectPortal = Config.dingding().getMessageRedirectPortal();
				if (messageRedirectPortal != null && !"".equals(messageRedirectPortal)) {
					String portal = "portalmobile.html?id=" + messageRedirectPortal;
					// 2021-11-1 딩톡 쪽에서 사용 불가. encode하면 서명이 통과되지 않음
					workUrl += "&redirectlink=" + portal;
				}
				// 2021-11-1 딩톡 쪽에서 사용 불가. encode하면 서명이 통과되지 않음
				LOGGER.debug("o2oa workUrl：" + workUrl);
				o2oaUrl = o2oaUrl + URLEncoder.encode(workUrl, DefaultCharset.name);
			} else {
				o2oaUrl = o2oaUrl + URLEncoder.encode(openPage, DefaultCharset.name);
			}
			LOGGER.info("o2oa 주소:" + o2oaUrl);
			return o2oaUrl+"&encode=true";
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
			String workUrl = Config.dingding().getWorkUrl();
			if (StringUtils.isNotEmpty(workUrl)) {
				return true;
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return false;
	}

	public static class DingdingMessageResp {

		private Integer errcode;
		private String errmsg;
		private Long task_id;

		public String getErrmsg() {
			return errmsg;
		}

		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}

		public Long getTask_id() {
			return task_id;
		}

		public void setTask_id(Long task_id) {
			this.task_id = task_id;
		}

		public Integer getErrcode() {
			return errcode;
		}

		public void setErrcode(Integer errcode) {
			this.errcode = errcode;
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
					o2oaUrl = o2oaUrl + URLEncoder.encode(  "cmsdocMobile.html?id=" + id, DefaultCharset.name);
				} else {
					o2oaUrl = o2oaUrl + URLEncoder.encode(openPage, DefaultCharset.name);
				}
				LOGGER.info("o2oa 업무 주소:" + o2oaUrl);
				return o2oaUrl+"&encode=true";
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
		public static String getCmsDocumentId(String messageBody) {
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
