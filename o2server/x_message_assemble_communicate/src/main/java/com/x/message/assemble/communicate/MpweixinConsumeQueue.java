package com.x.message.assemble.communicate;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Mpweixin;
import com.x.base.core.project.config.Mpweixin.MPweixinMessageTemp;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.message.assemble.communicate.message.WeixinTempMessage;
import com.x.message.assemble.communicate.message.WeixinTempMessage.WeixinTempMessageFieldObj;
import com.x.message.core.entity.Message;
import com.x.organization.core.entity.Person;

/**
 * 위챗 공식 계정 템플릿 메시지 발송 Created by fancyLou on 3/11/21. Copyright © 2021 O2. All rights
 * reserved.
 */
public class MpweixinConsumeQueue extends AbstractQueue<Message> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MpweixinConsumeQueue.class);

	private static final Gson gson = XGsonBuilder.instance();

	// creatorPerson 작성자 activityName 현재 노드 processName 프로세스 이름 startTime 시작 시간 title 제목

	@Override
	protected void execute(Message message) throws Exception {
		String tempId = Config.mpweixin().getTempMessageId();
		List<MPweixinMessageTemp> list = Config.mpweixin().getFieldList();
		if (Config.mpweixin().getEnable() && Config.mpweixin().getMessageEnable() && StringUtils.isNotEmpty(tempId)
				&& (list != null && !list.isEmpty())) {
			Person person = getPerson(message.getPerson());
			if (person != null) {
				String openId = person.getMpwxopenId();
				LOGGER.debug("openId:{}.", openId);
				if (StringUtils.isNotEmpty(openId)) {
					JsonObject object = gson.fromJson(message.getBody(), JsonObject.class);
					Map<String, WeixinTempMessageFieldObj> data = concreteData(message, object, list);
					String workUrl = getOpenUrl(message);
					WeixinTempMessage wxMessage = new WeixinTempMessage();
					wxMessage.setTouser(openId);
					wxMessage.setUrl(workUrl);
					wxMessage.setTemplate_id(tempId);
					wxMessage.setTopcolor("#fb4747");
					wxMessage.setData(data);
					LOGGER.debug("발송한 메시지 객체:{}.", wxMessage::toString);
					String url = Mpweixin.default_apiAddress + "/cgi-bin/message/template/send?access_token="
							+ Config.mpweixin().accessToken();
					WeixinResponse response = HttpConnection.postAsObject(url, null, wxMessage.toString(),
							WeixinResponse.class);
					LOGGER.debug("반환:{}.", response);
					if (response.getErrcode() != 0) {
						ExceptionQiyeweixinMessage e = new ExceptionQiyeweixinMessage(response.getErrcode(),
								response.getErrmsg());
						LOGGER.error(e);
					} else {
						success(message.getId());
					}
				} else {
					LOGGER.warn("위챗 공식 계정이 연동되지 않음:{}.", message::getPerson);
				}
			} else {
				LOGGER.warn("사용자를 찾을 수 없습니다.");
			}

		} else {
			LOGGER.warn("설정 파일의 설정 조건이 부족합니다.");
		}

	}

	/**
	 * 열기 주소 판단
	 *
	 * @param message
	 * @return
	 */
	private String getOpenUrl(Message message) {
		String openPage = DingdingConsumeQueue.OuterMessageHelper.getOpenPageUrl(message.getBody());
		if (StringUtils.isNotEmpty(openPage)) {
			return openPage;
		} else {
			String workId = DingdingConsumeQueue.OuterMessageHelper.getWorkIdFromBody(message.getBody());
			return getOpenWorkUrl(workId);
		}
	}

	private Map<String, WeixinTempMessageFieldObj> concreteData(Message message, JsonObject object,
			List<MPweixinMessageTemp> list) {
		Map<String, WeixinTempMessageFieldObj> data = new HashMap<>();
		// 고정 필드 first
		WeixinTempMessageFieldObj wobj = new WeixinTempMessageFieldObj();
		if (object.get("temp_first") != null) {
			String temp_first = object.get("temp_first").getAsString();
			wobj.setValue(temp_first);
		} else {
			wobj.setValue(message.getTitle());
		}
		data.put("first", wobj);
		// 동적 필드 keyword
		for (int i = 0; i < list.size(); i++) {
			MPweixinMessageTemp filed = list.get(i);
			String name = filed.getName();
			String tempName = filed.getTempName();
			String value = object.get(name).getAsString();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("파싱 결과 name:{}, value:{}, tempName:{}.", name, value, tempName);
			}
			if ("title".equalsIgnoreCase(name)) { // 업무 문서 제목이 비어 있으면 메시지 제목 사용
				if (StringUtils.isEmpty(value)) {
					value = "제목 없음";
				}
			} else {
				if ("creatorPerson".equalsIgnoreCase(name) && StringUtils.isNotEmpty(value)) {
					value = value.split("@")[0]; // 이름 추출
				}
				if (StringUtils.isEmpty(value)) {
					value = "unknown";
				}
			}
			WeixinTempMessageFieldObj obj = new WeixinTempMessageFieldObj();
			obj.setValue(value);
			obj.setColor("#173177");
			data.put(tempName, obj);
		}
		// 고정 필드 remark
		WeixinTempMessageFieldObj robj = new WeixinTempMessageFieldObj();
		if (object.get("temp_remark") != null) {
			String temp_remark = object.get("temp_remark").getAsString();
			robj.setValue(temp_remark);
		} else {
			robj.setValue("확인해 주세요!");
		}
		data.put("remark", robj);
		return data;
	}

	private Person getPerson(String credential) throws Exception {
		Person person;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			// openid로 사용자 조회
			person = business.message().getPersonWithCredential(credential);
		}
		return person;
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

	private String getOpenWorkUrl(String workId) {
		try {
			String o2oaUrl = Config.mpweixin().getWorkUrl();
			if (StringUtils.isEmpty(o2oaUrl)) {
				LOGGER.error(new Exception("workUrl 파라미터를 가져오지 못해 불가"));
				return null;
			}
			String workUrl = "workmobilewithaction.html?workid=" + workId;
			String portalId = Config.mpweixin().getPortalId();
			if (portalId != null && StringUtils.isNotEmpty(portalId.trim())) {
				String portal = "portalmobile.html?id=" + portalId;
				portal = URLEncoder.encode(portal, DefaultCharset.name);
				workUrl += "&redirectlink=" + portal;
			}
			workUrl = URLEncoder.encode(workUrl, DefaultCharset.name);
			o2oaUrl = o2oaUrl + "mpweixinsso.html?redirect=" + workUrl + "&type=login";
			o2oaUrl = URLEncoder.encode(o2oaUrl, DefaultCharset.name);
			String appId = Config.mpweixin().getAppid();
			String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appId + "&redirect_uri="
					+ o2oaUrl + "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("final url:{}.", url);
			}
			return url;
		} catch (Exception e) {
			LOGGER.error(e);
			return null;
		}

	}

	public static class WeixinResponse {
		private Integer errcode;
		private String errmsg;
		private Long msgid;

		public Integer getErrcode() {
			return errcode;
		}

		public void setErrcode(Integer errcode) {
			this.errcode = errcode;
		}

		public String getErrmsg() {
			return errmsg;
		}

		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}

		public Long getMsgid() {
			return msgid;
		}

		public void setMsgid(Long msgid) {
			this.msgid = msgid;
		}
	}

	
}
