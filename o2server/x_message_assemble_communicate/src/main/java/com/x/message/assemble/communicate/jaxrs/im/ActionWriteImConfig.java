package com.x.message.assemble.communicate.jaxrs.im;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.WebServers;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionWriteImConfig extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionWriteImConfig.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		if (!effectivePerson.isManager()) {
			throw new ExceptionConversationCheckError("권한이 없습니다");
		}
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		for (Map.Entry<String, JsonElement> en : Config.web().entrySet()) {
			map.put(en.getKey(), en.getValue());
		}
		map.put(IM_CONFIG_KEY_NAME, wi);
		String content = gson.toJson(map);
		String fileName = "web.json";
		WebConfigSaveWi saveWi = new WebConfigSaveWi();
		saveWi.setFileName(fileName);
		saveWi.setFileContent(content);
		ActionResponse response = CipherConnectionAction.post(false,
				Config.url_x_program_center_jaxrs("config", "save"), saveWi);
		if (response != null) {
			SaveConfigWo saveWo = response.getData(SaveConfigWo.class);
			if (saveWo != null && saveWo.getStatus() != null) {
				try {
					WebServers.updateWebServerConfigJson();
					wo.setValue(true);
					result.setData(wo);
				} catch (Exception e) {
					wo.setValue(false);
					result.setData(wo);
					LOGGER.error(e);
				}
			} else {
				wo.setValue(false);
				result.setData(wo);
			}
		} else {
			wo.setValue(false);
			result.setData(wo);
		}

		return result;
	}

	public static class WebConfigSaveWi extends GsonPropertyObject {
		
		private static final long serialVersionUID = -7474510912629527669L;
		
		private String fileName;
		private String fileContent;

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getFileContent() {
			return fileContent;
		}

		public void setFileContent(String fileContent) {
			this.fileContent = fileContent;
		}
	}

	/**
	 * IM 설정 파일. 이 설정 파일은 기본적으로 web.json key=imConfig에 기록됨
	 */
	static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -8090262801050845005L;
		
		@FieldDescribe("채팅 기록 비우기 기능 사용 여부.")
		private Boolean enableClearMsg;
		@FieldDescribe("채팅 메시지 회수 기능 사용 여부.")
		private Boolean enableRevokeMsg;
		@FieldDescribe("회수 시효(분)")
		private Integer revokeOutMinute;
		@FieldDescribe("대화 검사 스크립트.")
		private String conversationCheckInvoke;
		@FieldDescribe("onlyOffice로 파일 미리보기 사용 여부(onlyOffice 확장 앱 설치 필요).")
		private Boolean enableOnlyOfficePreview;
		@FieldDescribe("그룹 구성원 자율 나가기 기능 사용 여부.")
		private Boolean enableGroupMemberQuitSelf;
		@FieldDescribe("모든 최상위 단위 표시 여부.")
		private Boolean showAllTopUnit;


		public Boolean getShowAllTopUnit() {
			return showAllTopUnit;
		}

		public void setShowAllTopUnit(Boolean showAllTopUnit) {
			this.showAllTopUnit = showAllTopUnit;
		}

		public Boolean getEnableGroupMemberQuitSelf() {
			return enableGroupMemberQuitSelf;
		}

		public void setEnableGroupMemberQuitSelf(Boolean enableGroupMemberQuitSelf) {
			this.enableGroupMemberQuitSelf = enableGroupMemberQuitSelf;
		}

		public Boolean getEnableOnlyOfficePreview() {
			return enableOnlyOfficePreview;
		}

		public void setEnableOnlyOfficePreview(Boolean enableOnlyOfficePreview) {
			this.enableOnlyOfficePreview = enableOnlyOfficePreview;
		}

		public String getConversationCheckInvoke() {
			return conversationCheckInvoke;
		}

		public void setConversationCheckInvoke(String conversationCheckInvoke) {
			this.conversationCheckInvoke = conversationCheckInvoke;
		}

		public Integer getRevokeOutMinute() {
			return revokeOutMinute;
		}

		public void setRevokeOutMinute(Integer revokeOutMinute) {
			this.revokeOutMinute = revokeOutMinute;
		}

		public Boolean getEnableClearMsg() {
			return enableClearMsg;
		}

		public void setEnableClearMsg(Boolean enableClearMsg) {
			this.enableClearMsg = enableClearMsg;
		}

		public Boolean getEnableRevokeMsg() {
			return enableRevokeMsg;
		}

		public void setEnableRevokeMsg(Boolean enableRevokeMsg) {
			this.enableRevokeMsg = enableRevokeMsg;
		}
	}

	static class Wo extends WrapOutBoolean {

		private static final long serialVersionUID = 5620692994281685875L;

	}

	public static class SaveConfigWo extends GsonPropertyObject {

		private static final long serialVersionUID = -2114718746836340460L;

		@FieldDescribe("실행 시간")
		private String time;

		@FieldDescribe("실행 결과")
		private String status;

		@FieldDescribe("실행 메시지")
		private String message;

		@FieldDescribe("config 파일 내용")
		private String fileContent;

		@FieldDescribe("Sample 여부")
		private boolean isSample;

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getFileContent() {
			return fileContent;
		}

		public void setFileContent(String fileContent) {
			this.fileContent = fileContent;
		}

		public boolean isSample() {
			return isSample;
		}

		public void setSample(boolean isSample) {
			this.isSample = isSample;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

	}
}
