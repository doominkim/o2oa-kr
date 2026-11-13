package com.x.message.assemble.communicate;

import com.google.gson.Gson;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.WeLink;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.message.assemble.communicate.message.WeLinkMessage;
import com.x.message.core.entity.Message;
import com.x.organization.core.entity.Person;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WelinkConsumeQueue extends AbstractQueue<Message> {


	private static final Logger LOGGER = LoggerFactory.getLogger(WelinkConsumeQueue.class);
	private static final Gson gson = XGsonBuilder.instance();

	protected void execute(Message message) throws Exception {

		if (Config.weLink().getEnable() && Config.weLink().getMessageEnable()) {
			List<String> list = new ArrayList<>();
			String unique = this.getWeLinkId(message.getPerson());
			if (StringUtils.isEmpty(unique)) {
				LOGGER.error(new ExceptionWeLinkMessage("-1", "해당 welink 사용자 id를 찾을 수 없음"));
				return;
			}
			list.add(unique);
			WeLinkMessage m = new WeLinkMessage();
			m.setToUserList(list);
			m.setMsgRange("0");
			// 메시지 제목과 내용 처리
			if (message.getTitle().contains(":")) {
				int i = message.getTitle().indexOf(":");
				m.setMsgTitle(message.getTitle().substring(0, i));
				m.setMsgContent(message.getTitle().substring(i + 1));
			} else {
				m.setMsgTitle(message.getTitle());
				m.setMsgContent(message.getTitle());
			}
			m.setMsgOwner(appOwnerByType(message.getType()));
			m.setCreateTime(new Date().getTime() + "");
			// 하이퍼링크 추가 여부
			addHyperlink(message, m);
			String address = Config.weLink().getOapiAddress() + "/messages/v3/send";
			LOGGER.info("welink send url: {}" ,()-> address);
			List<NameValuePair> heads = new ArrayList<>();
			heads.add(new NameValuePair(WeLink.WeLink_Auth_Head_Key, Config.weLink().accessToken()));
			WeLinkMessageResp resp = HttpConnection.postAsObject(address, heads, m.toString(), WeLinkMessageResp.class);
			if ("0".equals(resp.getCode())) {
				ExceptionWeLinkMessage e = new ExceptionWeLinkMessage(resp.getCode(), resp.getMessage());
				LOGGER.error(e);
			} else {
				success(message.getId());
			}
		}
	}

	private void addHyperlink(Message message, WeLinkMessage m) {
		if (needTransferLink(message.getType())) {
			String workUrl = getOpenUrl(message);
			if (workUrl != null && !"".equals(workUrl)) {
				m.setUrlType("html");
				m.setUrlPath(workUrl);
			}
		}
	}

	private String getWeLinkId(String credential) throws Exception {
		String value = "";
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			// openid로 사용자 조회
			Person person = business.message().getPersonWithCredential(credential);
			if (null != person) {
				value = person.getWeLinkId();
			}
		}
		return value;
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
			openUrl = getWeiLinkOpenCMSDocumentUrl(message.getBody());
		} else { // 프로세스 업무 문서 관련
			openUrl = getWeiLinkOpenWorkUrl(message.getBody());
		}
		return openUrl;
	}


	/**
	 * 문서를 여는 url
	 *
	 * @param messageBody
	 * @return
	 */
	private String getWeiLinkOpenCMSDocumentUrl(String messageBody) {
		String o2oaUrl = null;
		try {
			o2oaUrl = Config.weLink().getWorkUrl() + "welinksso.html?redirect=";
			return DingdingConsumeQueue.OuterMessageHelper.getOpenCMSDocumentUrl(o2oaUrl, messageBody);
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
	private String getWeiLinkOpenWorkUrl(String messageBody) {
		try {
			String work = DingdingConsumeQueue.OuterMessageHelper.getWorkIdFromBody(messageBody);
			String o2oaUrl = Config.weLink().getWorkUrl();
			if (work == null || "".equals(work) || o2oaUrl == null || "".equals(o2oaUrl)) {
				return null;
			}
			String openPage = DingdingConsumeQueue.OuterMessageHelper.getOpenPageUrl(messageBody);
			if (StringUtils.isNotEmpty(openPage)) {
				o2oaUrl = o2oaUrl + "welinksso.html?redirect=" + openPage;
			} else {
				String workUrl = "workmobilewithaction.html?workid=" + work;
				String messageRedirectPortal = Config.weLink().getMessageRedirectPortal();
				if (messageRedirectPortal != null && !"".equals(messageRedirectPortal)) {
					String portal = "portalmobile.html?id=" + messageRedirectPortal;
					portal = URLEncoder.encode(portal, DefaultCharset.name);
					workUrl += "&redirectlink=" + portal;
				}
				workUrl = URLEncoder.encode(workUrl, DefaultCharset.name);
				o2oaUrl = o2oaUrl + "welinksso.html?redirect=" + workUrl;
			}
			return o2oaUrl;
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return "";
	}



	/**
	 * 딩톡 메시지를 markdown 형식 메시지로 변환할지 여부. 딩톡 업무 링크 설정 여부, 업무 메시지 여부에 따라 결정(현재 업무 메시지만 지원)
	 * 
	 * @param messageType 메시지 유형. 업무 메시지 여부 판단
	 * @return
	 */
	private boolean needTransferLink(String messageType) {
		try {
			String workUrl = Config.weLink().getWorkUrl();
			if (StringUtils.isNotEmpty(workUrl)) {
				return true;
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return false;
	}

	private String appOwnerByType(String messageType) {
		if (workMessageTypeList().contains(messageType)) {
			return "결재 메시지";
		} else {
			if (messageType.startsWith("meeting_")) {
				return "회의 메시지";
			} else if (messageType.startsWith("attachment_")) {
				return "파일 메시지";
			} else if (messageType.startsWith("calendar_")) {
				return "일정 메시지";
			} else if (messageType.startsWith("cms_")) {
				return "게시판 메시지";
			} else if (messageType.startsWith("bbs_")) {
				return "포럼 메시지";
			} else {
				return "메시지";
			}
		}
	}

	private List<String> workMessageTypeList() {
		List<String> list = new ArrayList<>();
		list.add(MessageConnector.TYPE_WORK_TO_WORKCOMPLETED);
		list.add(MessageConnector.TYPE_WORK_CREATE);
		list.add(MessageConnector.TYPE_WORK_DELETE);
		list.add(MessageConnector.TYPE_WORKCOMPLETED_CREATE);
		list.add(MessageConnector.TYPE_WORKCOMPLETED_DELETE);
		list.add(MessageConnector.TYPE_TASK_TO_TASKCOMPLETED);
		list.add(MessageConnector.TYPE_TASK_CREATE);
		list.add(MessageConnector.TYPE_TASK_DELETE);
		list.add(MessageConnector.TYPE_TASK_URGE);
		list.add(MessageConnector.TYPE_TASK_EXPIRE);
		list.add(MessageConnector.TYPE_TASK_PRESS);
		list.add(MessageConnector.TYPE_TASKCOMPLETED_CREATE);
		list.add(MessageConnector.TYPE_TASKCOMPLETED_DELETE);
		list.add(MessageConnector.TYPE_READ_TO_READCOMPLETED);
		list.add(MessageConnector.TYPE_READ_CREATE);
		list.add(MessageConnector.TYPE_READ_DELETE);
		list.add(MessageConnector.TYPE_READCOMPLETED_CREATE);
		list.add(MessageConnector.TYPE_READCOMPLETED_DELETE);
		list.add(MessageConnector.TYPE_REVIEW_CREATE);
		list.add(MessageConnector.TYPE_REVIEW_DELETE);

		return list;
	}

	public static class WeLinkMessageResp {

		private String code;
		private String message;
		private List<String> failedUserId;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public List<String> getFailedUserId() {
			return failedUserId;
		}

		public void setFailedUserId(List<String> failedUserId) {
			this.failedUserId = failedUserId;
		}
	}
}
