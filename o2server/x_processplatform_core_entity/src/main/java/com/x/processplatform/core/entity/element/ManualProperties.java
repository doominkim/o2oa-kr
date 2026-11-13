package com.x.processplatform.core.entity.element;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class ManualProperties extends JsonProperties {

	private static final long serialVersionUID = -8141148907781411801L;
	@FieldDescribe("활동 사용자 정의 데이터")
	private JsonElement customData;

	@FieldDescribe("추가 결재 허용 여부")
	private Boolean allowAddTask;

	@FieldDescribe("기본 결재자 추가 방식")
	private String defaultAddTaskType;

	@FieldDescribe("기본 처리 방식")
	private String defaultAddTaskMode;

	@FieldDescribe("동일 처리자의 다른 신분 결재 대기를 한 번에 병합 처리.")
	private Boolean processingTaskOnceUnderSamePerson;

	@FieldDescribe("반송 허용 여부.")
	private Boolean allowGoBack;

	// @since 8.2
	@FieldDescribe("종료 허용 여부.")
	private Boolean allowTerminate;

	@FieldDescribe("반송 설정.")
	private GoBackConfig goBackConfig;

	@FieldDescribe("수동 단계 업무 문서 진행 후 실행 스크립트.")
	private String manualAfterProcessingScript;

	@FieldDescribe("수동 단계 업무 문서 진행 후 실행 스크립트 텍스트.")
	private String manualAfterProcessingScriptText;

	@FieldDescribe("프로세스 참여자를 결재 대기자로 설정.")
	private Participant taskParticipant;

	public String getDefaultAddTaskType() {
		return defaultAddTaskType;
	}

	public void setDefaultAddTaskType(String defaultAddTaskType) {
		this.defaultAddTaskType = defaultAddTaskType;
	}

	public String getDefaultAddTaskMode() {
		return defaultAddTaskMode;
	}

	public void setDefaultAddTaskMode(String defaultAddTaskMode) {
		this.defaultAddTaskMode = defaultAddTaskMode;
	}

	public Boolean getAllowTerminate() {
		return allowTerminate;
	}

	public void setAllowTerminate(Boolean allowTerminate) {
		this.allowTerminate = allowTerminate;
	}

	public Participant getTaskParticipant() {
		return taskParticipant;
	}

	public void setTaskParticipant(Participant participant) {
		this.taskParticipant = participant;
	}

	public String getManualAfterProcessingScript() {
		return manualAfterProcessingScript;
	}

	public void setManualAfterProcessingScript(String manualAfterProcessingScript) {
		this.manualAfterProcessingScript = manualAfterProcessingScript;
	}

	public String getManualAfterProcessingScriptText() {
		return manualAfterProcessingScriptText;
	}

	public void setManualAfterProcessingScriptText(String manualAfterProcessingScriptText) {
		this.manualAfterProcessingScriptText = manualAfterProcessingScriptText;
	}

	public Boolean getAllowGoBack() {
		return allowGoBack;
	}

	public void setAllowGoBack(Boolean allowGoBack) {
		this.allowGoBack = allowGoBack;
	}

	public GoBackConfig getGoBackConfig() {
		if (null == goBackConfig) {
			this.goBackConfig = new GoBackConfig();
		}
		return this.goBackConfig;
	}

	public void setGoBackConfig(GoBackConfig goBackConfig) {
		this.goBackConfig = goBackConfig;
	}

	public JsonElement getCustomData() {
		return customData;
	}

	public void setCustomData(JsonElement customData) {
		this.customData = customData;
	}

	public Boolean getAllowAddTask() {
		return allowAddTask;
	}

	public void setAllowAddTask(Boolean allowAddTask) {
		this.allowAddTask = allowAddTask;
	}

	public Boolean getProcessingTaskOnceUnderSamePerson() {
		return processingTaskOnceUnderSamePerson;
	}

	public void setProcessingTaskOnceUnderSamePerson(Boolean processingTaskOnceUnderSamePerson) {
		this.processingTaskOnceUnderSamePerson = processingTaskOnceUnderSamePerson;
	}

	public static class GoBackConfig extends GsonPropertyObject {

		private static final long serialVersionUID = -2057532080381874273L;

		public static final String TYPE_PREV = "prev";
		public static final String TYPE_ANY = "any";
		public static final String TYPE_DEFINE = "define";

		public static final String WAY_STEP = "step";
		public static final String WAY_JUMP = "jump";
		public static final String WAY_CUSTOM = "custom";
		public static final String WAY_DEFAULT = "default";

		@FieldDescribe("유형, prev: 이전 단계로 반송, any: 임의 단계, define: 설정.")
		private String type;

		@FieldDescribe("경로 방식, step: 정상 진행, jump: 반송을 시작한 단계로 이동, custom: 사용자 지정.")
		private String way;

		@FieldDescribe("수동 단계 병렬 모드에서 결재 대기가 여러 건일 때 반송 허용 여부, 이 설정은 수동 단계 병렬 모드에만 적용.")
		private Boolean multiTaskEnable;

		public Boolean getMultiTaskEnable() {
			return multiTaskEnable;
		}

		public void setMultiTaskEnable(Boolean multiTaskEnable) {
			this.multiTaskEnable = multiTaskEnable;
		}

		private List<DefineConfig> defineConfigList = new ArrayList<>();

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getWay() {
			return way;
		}

		public void setWay(String way) {
			this.way = way;
		}

		public List<DefineConfig> getDefineConfigList() {
			return defineConfigList;
		}

		public void setDefineConfigList(List<DefineConfig> defineConfigList) {
			this.defineConfigList = defineConfigList;
		}

	}

	public static class DefineConfig extends GsonPropertyObject {

		private static final long serialVersionUID = 8022864967621176462L;

		private String activity;

		private String way;

		public String getActivity() {
			return activity;
		}

		public void setActivity(String activity) {
			this.activity = activity;
		}

		public String getWay() {
			return way;
		}

		public void setWay(String way) {
			this.way = way;
		}

	}

	public static class Participant extends GsonPropertyObject {

		private static final long serialVersionUID = 8552496027679106938L;

		public static final String TYPE_CREATOR = "creator";
		public static final String TYPE_ACTIVITY = "activity";
		public static final String TYPE_MAINTENANCE = "maintenance";

		@FieldDescribe("creator: work 생성자, activity: 프로세스 단계 처리 완료 신분(진행 참여), maintenance: 프로세스 관리 신분. 빈 값 및 기타 값은 무시.")
		private String type;

		@FieldDescribe("type=activity, data는 활동 단계 id(배열).")
		private JsonElement data;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public JsonElement getData() {
			return data;
		}

		public void setData(JsonElement data) {
			this.data = data;
		}

	}

}
