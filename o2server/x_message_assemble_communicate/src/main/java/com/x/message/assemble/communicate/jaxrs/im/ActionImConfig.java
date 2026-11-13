package com.x.message.assemble.communicate.jaxrs.im;

import java.util.Map;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * Created by fancyLou on 2022/2/18. Copyright © 2022 O2. All rights reserved.
 */
public class ActionImConfig extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionImConfig.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setEnableClearMsg(false);
		wo.setEnableRevokeMsg(false);
		for (Map.Entry<String, JsonElement> en : Config.web().entrySet()) {
			if (en.getKey().equals(IM_CONFIG_KEY_NAME)) {
				JsonElement je = en.getValue();
				wo = this.convertToWrapIn(je, Wo.class);
			}
		}
		if (wo.getRevokeOutMinute() == null || wo.getRevokeOutMinute() <= 0) {
			wo.setRevokeOutMinute(2); // 기본 2분
		}
		wo.setVersionNo(310);
		wo.setChangelog("그룹 멤버 직접 나가기 등 기능이 추가되었습니다!");
		result.setData(wo);
		return result;
	}

	static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 6217331123000062134L;

		@FieldDescribe("그룹 구성원 자율 나가기 기능 사용 여부.")
		private Boolean enableGroupMemberQuitSelf;
		@FieldDescribe("채팅 기록 비우기 기능 사용 여부.")
		private Boolean enableClearMsg;
		@FieldDescribe("채팅 메시지 회수 기능 사용 여부.")
		private Boolean enableRevokeMsg;
		@FieldDescribe("onlyOffice로 파일 미리보기 사용 여부(onlyOffice 확장 앱 설치 필요).")
		private Boolean enableOnlyOfficePreview;
		@FieldDescribe("모든 최상위 단위 표시 여부.")
		private Boolean showAllTopUnit;
		@FieldDescribe("회수 시효(분)")
		private Integer revokeOutMinute;
		@FieldDescribe("대화 검사 스크립트.")
		private String conversationCheckInvoke;
		@FieldDescribe("버전 번호.")
		private int versionNo;
		@FieldDescribe("업데이트 내용.")
		private String changelog;

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

		public int getVersionNo() {
			return versionNo;
		}

		public void setVersionNo(int versionNo) {
			this.versionNo = versionNo;
		}

		public String getChangelog() {
			return changelog;
		}

		public void setChangelog(String changelog) {
			this.changelog = changelog;
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
}
