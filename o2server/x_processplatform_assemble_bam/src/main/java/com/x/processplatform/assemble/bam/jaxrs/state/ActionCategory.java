package com.x.processplatform.assemble.bam.jaxrs.state;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.bam.ThisApplication;

class ActionCategory extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionCategory.class);

	ActionResult<Wo> execute() throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		result.setData(ThisApplication.state.getCategory());
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("앱 분류")
		private List<WoApplication> application = new ArrayList<>();

		@FieldDescribe("분류 프로세스")
		private List<WoProcess> process = new ArrayList<>();

		@FieldDescribe("활동 분류")
		private List<WoActivity> activity = new ArrayList<>();

		public List<WoApplication> getApplication() {
			return application;
		}

		public void setApplication(List<WoApplication> application) {
			this.application = application;
		}

		public List<WoProcess> getProcess() {
			return process;
		}

		public void setProcess(List<WoProcess> process) {
			this.process = process;
		}

		public List<WoActivity> getActivity() {
			return activity;
		}

		public void setActivity(List<WoActivity> activity) {
			this.activity = activity;
		}

	}

	public static class WoApplication extends GsonPropertyObject {

		@FieldDescribe("앱 이름")
		private String name;

		@FieldDescribe("앱 식별자")
		private String value;

		@FieldDescribe("결재 대기 수")
		private Long taskCount;

		@FieldDescribe("기한 초과 결재 대기 수")
		private Long taskExpiredCount;

		@FieldDescribe("결재 대기 총 소요 시간")
		private Long taskDuration;

		@FieldDescribe("완료된 결재 대기 수")
		private Long taskCompletedCount;

		@FieldDescribe("완료된 기한 초과 결재 대기 수")
		private Long taskCompletedExpiredCount;

		@FieldDescribe("완료된 결재 대기 총 소요 시간")
		private Long taskCompletedDuration;

		@FieldDescribe("업무 문서 수")
		private Long workCount;

		@FieldDescribe("기한 초과 업무 문서 수")
		private Long workExpiredCount;

		@FieldDescribe("업무 문서 총 소요 시간")
		private Long workDuration;

		@FieldDescribe("완료된 업무 문서 수")
		private Long workCompletedCount;

		@FieldDescribe("완료된 기한 초과 업무 문서 수")
		private Long workCompletedExpiredCount;

		@FieldDescribe("완료된 업무 문서 총 소요 시간")
		private Long workCompletedDuration;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public Long getTaskCount() {
			return taskCount;
		}

		public void setTaskCount(Long taskCount) {
			this.taskCount = taskCount;
		}

		public Long getTaskExpiredCount() {
			return taskExpiredCount;
		}

		public void setTaskExpiredCount(Long taskExpiredCount) {
			this.taskExpiredCount = taskExpiredCount;
		}

		public Long getTaskDuration() {
			return taskDuration;
		}

		public void setTaskDuration(Long taskDuration) {
			this.taskDuration = taskDuration;
		}

		public Long getTaskCompletedCount() {
			return taskCompletedCount;
		}

		public void setTaskCompletedCount(Long taskCompletedCount) {
			this.taskCompletedCount = taskCompletedCount;
		}

		public Long getTaskCompletedExpiredCount() {
			return taskCompletedExpiredCount;
		}

		public void setTaskCompletedExpiredCount(Long taskCompletedExpiredCount) {
			this.taskCompletedExpiredCount = taskCompletedExpiredCount;
		}

		public Long getTaskCompletedDuration() {
			return taskCompletedDuration;
		}

		public void setTaskCompletedDuration(Long taskCompletedDuration) {
			this.taskCompletedDuration = taskCompletedDuration;
		}

		public Long getWorkCount() {
			return workCount;
		}

		public void setWorkCount(Long workCount) {
			this.workCount = workCount;
		}

		public Long getWorkExpiredCount() {
			return workExpiredCount;
		}

		public void setWorkExpiredCount(Long workExpiredCount) {
			this.workExpiredCount = workExpiredCount;
		}

		public Long getWorkDuration() {
			return workDuration;
		}

		public void setWorkDuration(Long workDuration) {
			this.workDuration = workDuration;
		}

		public Long getWorkCompletedCount() {
			return workCompletedCount;
		}

		public void setWorkCompletedCount(Long workCompletedCount) {
			this.workCompletedCount = workCompletedCount;
		}

		public Long getWorkCompletedExpiredCount() {
			return workCompletedExpiredCount;
		}

		public void setWorkCompletedExpiredCount(Long workCompletedExpiredCount) {
			this.workCompletedExpiredCount = workCompletedExpiredCount;
		}

		public Long getWorkCompletedDuration() {
			return workCompletedDuration;
		}

		public void setWorkCompletedDuration(Long workCompletedDuration) {
			this.workCompletedDuration = workCompletedDuration;
		}

	}

	public static class WoProcess extends GsonPropertyObject {

		@FieldDescribe("프로세스 이름")
		private String name;

		@FieldDescribe("프로세스 식별자")
		private String value;

		@FieldDescribe("앱 이름")
		private String applicationName;

		@FieldDescribe("앱 식별자")
		private String applicationValue;

		@FieldDescribe("결재 대기 수")
		private Long taskCount;

		@FieldDescribe("기한 초과 결재 대기 수")
		private Long taskExpiredCount;

		@FieldDescribe("결재 대기 총 소요 시간")
		private Long taskDuration;

		@FieldDescribe("처리 완료 수")
		private Long taskCompletedCount;

		@FieldDescribe("기한 초과 처리 완료 수")
		private Long taskCompletedExpiredCount;

		@FieldDescribe("처리 완료 총 소요 시간")
		private Long taskCompletedDuration;

		@FieldDescribe("업무 문서 수")
		private Long workCount;

		@FieldDescribe("기한 초과 업무 문서 수")
		private Long workExpiredCount;

		@FieldDescribe("업무 문서 총 소요 시간")
		private Long workDuration;

		@FieldDescribe("완료된 업무 문서 수")
		private Long workCompletedCount;

		@FieldDescribe("기한 초과 완료 업무 문서 수")
		private Long workCompletedExpiredCount;

		@FieldDescribe("완료된 업무 문서 총 소요 시간")
		private Long workCompletedDuration;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getApplicationName() {
			return applicationName;
		}

		public void setApplicationName(String applicationName) {
			this.applicationName = applicationName;
		}

		public String getApplicationValue() {
			return applicationValue;
		}

		public void setApplicationValue(String applicationValue) {
			this.applicationValue = applicationValue;
		}

		public Long getTaskCount() {
			return taskCount;
		}

		public void setTaskCount(Long taskCount) {
			this.taskCount = taskCount;
		}

		public Long getTaskExpiredCount() {
			return taskExpiredCount;
		}

		public void setTaskExpiredCount(Long taskExpiredCount) {
			this.taskExpiredCount = taskExpiredCount;
		}

		public Long getTaskDuration() {
			return taskDuration;
		}

		public void setTaskDuration(Long taskDuration) {
			this.taskDuration = taskDuration;
		}

		public Long getTaskCompletedCount() {
			return taskCompletedCount;
		}

		public void setTaskCompletedCount(Long taskCompletedCount) {
			this.taskCompletedCount = taskCompletedCount;
		}

		public Long getTaskCompletedExpiredCount() {
			return taskCompletedExpiredCount;
		}

		public void setTaskCompletedExpiredCount(Long taskCompletedExpiredCount) {
			this.taskCompletedExpiredCount = taskCompletedExpiredCount;
		}

		public Long getTaskCompletedDuration() {
			return taskCompletedDuration;
		}

		public void setTaskCompletedDuration(Long taskCompletedDuration) {
			this.taskCompletedDuration = taskCompletedDuration;
		}

		public Long getWorkCount() {
			return workCount;
		}

		public void setWorkCount(Long workCount) {
			this.workCount = workCount;
		}

		public Long getWorkExpiredCount() {
			return workExpiredCount;
		}

		public void setWorkExpiredCount(Long workExpiredCount) {
			this.workExpiredCount = workExpiredCount;
		}

		public Long getWorkDuration() {
			return workDuration;
		}

		public void setWorkDuration(Long workDuration) {
			this.workDuration = workDuration;
		}

		public Long getWorkCompletedCount() {
			return workCompletedCount;
		}

		public void setWorkCompletedCount(Long workCompletedCount) {
			this.workCompletedCount = workCompletedCount;
		}

		public Long getWorkCompletedExpiredCount() {
			return workCompletedExpiredCount;
		}

		public void setWorkCompletedExpiredCount(Long workCompletedExpiredCount) {
			this.workCompletedExpiredCount = workCompletedExpiredCount;
		}

		public Long getWorkCompletedDuration() {
			return workCompletedDuration;
		}

		public void setWorkCompletedDuration(Long workCompletedDuration) {
			this.workCompletedDuration = workCompletedDuration;
		}

	}

	public static class WoActivity extends GsonPropertyObject {
		@FieldDescribe("프로세스 이름")
		private String name;

		@FieldDescribe("프로세스 식별자")
		private String value;

		@FieldDescribe("앱 이름")
		private String applicationName;

		@FieldDescribe("앱 식별자")
		private String applicationValue;

		@FieldDescribe("프로세스 이름")
		private String processName;

		@FieldDescribe("프로세스 식별자")
		private String processValue;

		@FieldDescribe("결재 대기 수")
		private Long taskCount;

		@FieldDescribe("기한 초과 결재 대기 수")
		private Long taskExpiredCount;

		@FieldDescribe("결재 대기 총 소요 시간")
		private Long taskDuration;

		@FieldDescribe("처리 완료 수")
		private Long taskCompletedCount;

		@FieldDescribe("기한 초과 처리 완료 수")
		private Long taskCompletedExpiredCount;

		@FieldDescribe("처리 완료 총 소요 시간")
		private Long taskCompletedDuration;

		@FieldDescribe("업무 문서 수")
		private Long workCount;

		@FieldDescribe("기한 초과 업무 문서 수")
		private Long workExpiredCount;

		@FieldDescribe("업무 문서 총 소요 시간")
		private Long workDuration;

		// @FieldDescribe("완료된 업무 문서 수")
		// private Long workCompletedCount;
		//
		// @FieldDescribe("기한 초과 완료 업무 문서 수")
		// private Long workCompletedExpiredCount;
		//
		// @FieldDescribe("완료된 업무 문서 총 소요 시간")
		// private Long workCompletedDuration;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getApplicationName() {
			return applicationName;
		}

		public void setApplicationName(String applicationName) {
			this.applicationName = applicationName;
		}

		public String getApplicationValue() {
			return applicationValue;
		}

		public void setApplicationValue(String applicationValue) {
			this.applicationValue = applicationValue;
		}

		public Long getTaskCount() {
			return taskCount;
		}

		public void setTaskCount(Long taskCount) {
			this.taskCount = taskCount;
		}

		public Long getTaskExpiredCount() {
			return taskExpiredCount;
		}

		public void setTaskExpiredCount(Long taskExpiredCount) {
			this.taskExpiredCount = taskExpiredCount;
		}

		public Long getTaskDuration() {
			return taskDuration;
		}

		public void setTaskDuration(Long taskDuration) {
			this.taskDuration = taskDuration;
		}

		public Long getTaskCompletedCount() {
			return taskCompletedCount;
		}

		public void setTaskCompletedCount(Long taskCompletedCount) {
			this.taskCompletedCount = taskCompletedCount;
		}

		public Long getTaskCompletedExpiredCount() {
			return taskCompletedExpiredCount;
		}

		public void setTaskCompletedExpiredCount(Long taskCompletedExpiredCount) {
			this.taskCompletedExpiredCount = taskCompletedExpiredCount;
		}

		public Long getTaskCompletedDuration() {
			return taskCompletedDuration;
		}

		public void setTaskCompletedDuration(Long taskCompletedDuration) {
			this.taskCompletedDuration = taskCompletedDuration;
		}

		public Long getWorkCount() {
			return workCount;
		}

		public void setWorkCount(Long workCount) {
			this.workCount = workCount;
		}

		public Long getWorkExpiredCount() {
			return workExpiredCount;
		}

		public void setWorkExpiredCount(Long workExpiredCount) {
			this.workExpiredCount = workExpiredCount;
		}

		public Long getWorkDuration() {
			return workDuration;
		}

		public void setWorkDuration(Long workDuration) {
			this.workDuration = workDuration;
		}

		public String getProcessName() {
			return processName;
		}

		public void setProcessName(String processName) {
			this.processName = processName;
		}

		public String getProcessValue() {
			return processValue;
		}

		public void setProcessValue(String processValue) {
			this.processValue = processValue;
		}

	}

}
