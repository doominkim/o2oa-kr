package com.x.message.assemble.communicate;

import java.net.URLEncoder;

import com.x.base.core.project.message.MessageConnector;
import com.x.message.assemble.communicate.message.QiyeweixinBaseMessage;
import com.x.message.assemble.communicate.message.QiyeweixinTextCardMessage;
import com.x.message.assemble.communicate.message.QiyeweixinTextMessage;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.message.core.entity.Message;

public class QiyeweixinConsumeQueue extends AbstractQueue<Message> {

	private static final Logger LOGGER = LoggerFactory.getLogger(QiyeweixinConsumeQueue.class);

	protected void execute(Message message) throws Exception {

		if (Config.qiyeweixin().getEnable() && Config.qiyeweixin().getMessageEnable()) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				QiyeweixinBaseMessage m = generateMessage(message, business);
				String address = Config.qiyeweixin().getApiAddress() + "/cgi-bin/message/send?access_token="
						+ Config.qiyeweixin().corpAccessToken();
				QiyeweixinMessageResp resp = HttpConnection.postAsObject(address, null, m.toString(),
						QiyeweixinMessageResp.class);
				if (resp.getErrcode() != 0) {
					ExceptionQiyeweixinMessage e = new ExceptionQiyeweixinMessage(resp.getErrcode(), resp.getErrmsg());
					LOGGER.error(e);
				} else {
					Message messageEntityObject = emc.find(message.getId(), Message.class);
					if (null != messageEntityObject) {
						emc.beginTransaction(Message.class);
						messageEntityObject.setConsumed(true);
						emc.commit();
					}
				}
			}
		}
	}

	private QiyeweixinBaseMessage generateMessage(Message message, Business business) throws Exception {
		String content = message.getTitle();
		String workUrl = getOpenUrl(message);
		// 하이퍼링크가 있으면 카드 메시지 발송. 현재 콘텐츠 관리와 프로세스 지원
		if (needTransferLink(message.getType()) && StringUtils.isNotEmpty(workUrl)) {
			QiyeweixinTextCardMessage cardMessage = new QiyeweixinTextCardMessage();
			cardMessage.setAgentid(Long.parseLong(Config.qiyeweixin().getAgentId(), 10));
			cardMessage.setTouser(business.organization().person().getObject(message.getPerson()).getQiyeweixinId());
			// 콘텐츠 관리
			if (MessageConnector.TYPE_CMS_PUBLISH.equals(message.getType())
					|| MessageConnector.TYPE_CMS_PUBLISH_TO_CREATOR.equals(message.getType())) {
				String categoryName = DingdingConsumeQueue.OuterMessageHelper.getPropertiesFromBody("categoryName", message.getBody());
				if (StringUtils.isEmpty(categoryName)) {
					categoryName = "게시판 알림";
				}
				cardMessage.getTextcard().setTitle("【"+categoryName+"】");
			} else {
				String processName = DingdingConsumeQueue.OuterMessageHelper.getPropertiesFromBody("processName", message.getBody());
				if (StringUtils.isEmpty(processName)) {
					processName = "결재 알림";
				}
				cardMessage.getTextcard().setTitle("【"+processName+"】");
			}
			cardMessage.getTextcard().setDescription(message.getTitle());
			cardMessage.getTextcard().setUrl(workUrl);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("위챗 카드 메시지:{}", cardMessage::toString);
			}
			return cardMessage;

		} else { // 그 외는 일반 텍스트 메시지
			QiyeweixinTextMessage textMessage = new QiyeweixinTextMessage();
			textMessage.setAgentid(Long.parseLong(Config.qiyeweixin().getAgentId(), 10));
			textMessage.setTouser(business.organization().person().getObject(message.getPerson()).getQiyeweixinId());
			textMessage.getText().setContent(content);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("위챗 텍스트 메시지:{}", textMessage::toString);
			}
			return textMessage;
		}

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
			openUrl = getQywxOpenCMSDocumentUrl(message.getBody());
		} else { // 프로세스 업무 문서 관련
			openUrl = getQywxOpenWorkUrl(message.getBody());
		}
		return openUrl;
	}

	/**
	 * 문서를 여는 url
	 *
	 * @param messageBody
	 * @return
	 */
	private String getQywxOpenCMSDocumentUrl(String messageBody) {
		String o2oaUrl = null;
		try {
			String corpId = Config.qiyeweixin().getCorpId();
			String agentId = Config.qiyeweixin().getAgentId();
			o2oaUrl = Config.qiyeweixin().getWorkUrl() + "qiyeweixinsso.html?redirect=";
			o2oaUrl = DingdingConsumeQueue.OuterMessageHelper.getOpenCMSDocumentUrl(o2oaUrl, messageBody);
			if (StringUtils.isEmpty(o2oaUrl) || StringUtils.isEmpty(corpId) || StringUtils.isEmpty(agentId)) {
				return null;
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("o2oa 주소:{}" , o2oaUrl);
			}
			o2oaUrl = URLEncoder.encode(o2oaUrl, DefaultCharset.name);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("encode url : {}", o2oaUrl);
			}
			String oauthUrl = Config.qiyeweixin().getOauth2Address();
			String url = oauthUrl + "/connect/oauth2/authorize?appid=" + corpId
					+ "&response_type=code&scope=snsapi_base" + "&agentid=" + agentId + "&redirect_uri=" + o2oaUrl
					+ "&#wechat_redirect";
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("final url : {}" , url);
			}
			return url;
		} catch (Exception e) {
			LOGGER.error(e);
			return null;
		}
	}

	/**
	 * SSO 및 업무 문서 열기 주소 생성
	 * 
	 * @param messageBody
	 * @return
	 */
	private String getQywxOpenWorkUrl(String messageBody) {
		try {
			String openPage = DingdingConsumeQueue.OuterMessageHelper.getOpenPageUrl(messageBody);
			String work = DingdingConsumeQueue.OuterMessageHelper.getWorkIdFromBody(messageBody);
			String o2oaUrl = Config.qiyeweixin().getWorkUrl();
			String corpId = Config.qiyeweixin().getCorpId();
			String agentId = Config.qiyeweixin().getAgentId();
			if (StringUtils.isEmpty(o2oaUrl) || StringUtils.isEmpty(corpId) || StringUtils.isEmpty(agentId)) {
				LOGGER.warn("workurl corpid agentid가 비어 있을 수 있습니다!");
				return null;
			}
			if (StringUtils.isNotEmpty(openPage)) {
				o2oaUrl = o2oaUrl + "qiyeweixinsso.html?redirect=" +  URLEncoder.encode(openPage, DefaultCharset.name);
			} else if (StringUtils.isNotEmpty(work) ) {
				String workUrl = "workmobilewithaction.html?workid=" + work;
				String messageRedirectPortal = Config.qiyeweixin().getMessageRedirectPortal();
				if (messageRedirectPortal != null && !messageRedirectPortal.trim().isEmpty()) {
					String portal = "portalmobile.html?id=" + messageRedirectPortal.trim();
					portal = URLEncoder.encode(portal, DefaultCharset.name);
					workUrl += "&redirectlink=" + portal;
				}
				workUrl = URLEncoder.encode(workUrl, DefaultCharset.name);
				o2oaUrl = o2oaUrl + "qiyeweixinsso.html?redirect=" + workUrl;
			} else {
				LOGGER.warn("work 또는 openPageUrl이 없어 주소를 생성할 수 없습니다!");
				return null;
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("o2oa 주소:{}", o2oaUrl);
			}
			o2oaUrl = URLEncoder.encode(o2oaUrl, DefaultCharset.name);
			String oauthUrl = Config.qiyeweixin().getOauth2Address();
			String url = oauthUrl + "/connect/oauth2/authorize?appid=" + corpId
					+ "&response_type=code&scope=snsapi_base" + "&agentid=" + agentId + "&redirect_uri=" + o2oaUrl
					+ "&#wechat_redirect";
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("final url : {}" , url);
			}
			return url;
		} catch (Exception e) {
			LOGGER.error(e);
		}

		return "";
	}


	/**
	 * 위챗 워크 메시지를 하이퍼링크 메시지로 변환할지 여부. 위챗 워크 앱 링크 설정 여부, 업무 메시지 여부에 따라 결정(현재 업무 메시지만 지원)
	 * 
	 * @param messageType 메시지 유형. 업무 메시지 여부 판단
	 * @return
	 */
	private boolean needTransferLink(String messageType) {
		try {
			String workUrl = Config.qiyeweixin().getWorkUrl();
			if (StringUtils.isNotEmpty(workUrl)) {
				return true;
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return false;
	}



	public static class QiyeweixinMessageResp {

		/**
		 * <code>	 {
		 * "errcode" : 0,
		 * "errmsg" : "ok",
		 * "invaliduser" : "userid1|userid2", // 대소문자 구분 없음, 반환 목록은 모두 소문자로 변환
		 * "invalidparty" : "partyid1|partyid2",
		 * "invalidtag":"tagid1|tagid2"
		 * }
		 * </code>
		 */

		private Integer errcode;
		private String errmsg;
		private String invaliduser;
		private String invalidparty;
		private String invalidtag;

		public String getErrmsg() {
			return errmsg;
		}

		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}

		public String getInvaliduser() {
			return invaliduser;
		}

		public void setInvaliduser(String invaliduser) {
			this.invaliduser = invaliduser;
		}

		public String getInvalidparty() {
			return invalidparty;
		}

		public void setInvalidparty(String invalidparty) {
			this.invalidparty = invalidparty;
		}

		public String getInvalidtag() {
			return invalidtag;
		}

		public void setInvalidtag(String invalidtag) {
			this.invalidtag = invalidtag;
		}

		public Integer getErrcode() {
			return errcode;
		}

		public void setErrcode(Integer errcode) {
			this.errcode = errcode;
		}

	}
}
