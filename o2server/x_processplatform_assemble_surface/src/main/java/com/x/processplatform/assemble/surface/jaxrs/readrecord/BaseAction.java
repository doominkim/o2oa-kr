package com.x.processplatform.assemble.surface.jaxrs.readrecord;

import java.util.Date;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.core.entity.element.ActivityType;

abstract class BaseAction extends StandardJaxrsAction {

	public static class ReadRecord extends GsonPropertyObject {

		private static final long serialVersionUID = -7967027122452291358L;

		public static final String TYPE_READ = "read";
		public static final String TYPE_READ_COMPLETED = "readCompleted";

		@FieldDescribe("열람 대기 또는 열람 완료 식별자.")
		private String id;

		@FieldDescribe("유형:read|readCompleted.")
		private String type;

		@FieldDescribe("작업 식별자.")
		private String job;

		@FieldDescribe("업무 문서 식별자.")
		private String work;

		@FieldDescribe("완료된 업무 문서 식별자.")
		private String workCompleted;

		@FieldDescribe("업무 문서 완료 여부.")
		private Boolean completed;

		@FieldDescribe("열람 대기 식별자.")
		private String read;

		@FieldDescribe("제목.")
		private String title;

		@FieldDescribe("앱 식별자.")
		private String application;

		@FieldDescribe("앱 이름.")
		private String applicationName;

		@FieldDescribe("앱 별칭.")
		private String applicationAlias;

		@FieldDescribe("프로세스 식별자.")
		private String process;

		@FieldDescribe("프로세스 이름.")
		private String processName;

		@FieldDescribe("프로세스 별칭.")
		private String processAlias;

		@FieldDescribe("번호.")
		private String serial;

		@FieldDescribe("현재 처리자.")
		private String person;

		@FieldDescribe("현재 처리자 신분.")
		private String identity;

		@FieldDescribe("현재 처리자 소속 조직.")
		private String unit;

		@FieldDescribe("처리 의견.")
		protected String opinion;

		@FieldDescribe("활동 식별자.")
		private String activity;

		@FieldDescribe("활동 이름.")
		private String activityName;

		@FieldDescribe("활동 별칭.")
		private String activityAlias;

		@FieldDescribe("활동 설명.")
		private String activityDescription;

		@FieldDescribe("활동 유형.")
		private ActivityType activityType;

		@FieldDescribe("활동 Token.")
		private String activityToken;

		@FieldDescribe("생성자")
		private String creatorPerson;

		@FieldDescribe("작성자 Identity")
		private String creatorIdentity;

		@FieldDescribe("작성자 조직")
		private String creatorUnit;

		@FieldDescribe("시작 시간.")
		private Date startTime;

		@FieldDescribe("Filter에서 분류에 사용.")
		private String startTimeMonth;

		@FieldDescribe("read가 readCompleted로 전환된 완료 시간, 즉 작업 완료 시간.")
		private Date completedTime;

		@FieldDescribe("read가 readCompleted로 전환된 완료 월, 즉 작업 완료 시간, filter 필터링에 사용..")
		private String completedTimeMonth;

		@FieldDescribe("업무 소요 시간(분).")
		private Long duration;

		@FieldDescribe("현재 활동 이름.")
		private String currentActivityName;

		@FieldDescribe("업무 데이터 String 값01.")
		private String stringValue01;

		@FieldDescribe("업무 데이터 String 값02.")
		private String stringValue02;

		@FieldDescribe("업무 데이터 String 값03.")
		private String stringValue03;

		@FieldDescribe("업무 데이터 String 값04.")
		private String stringValue04;

		@FieldDescribe("업무 데이터 String 값05.")
		private String stringValue05;

		@FieldDescribe("업무 데이터 String 값06.")
		private String stringValue06;

		@FieldDescribe("업무 데이터 Boolean 값01.")
		private Boolean booleanValue01;

		@FieldDescribe("업무 데이터 Boolean 값02.")
		private Boolean booleanValue02;

		@FieldDescribe("업무 데이터 Double 값01.")
		private Double doubleValue01;

		@FieldDescribe("업무 데이터 Double 값02.")
		private Double doubleValue02;

		@FieldDescribe("업무 데이터 Long 값01.")
		private Long longValue01;

		@FieldDescribe("업무 데이터 Long 값02.")
		private Long longValue02;

		@FieldDescribe("업무 데이터 DateTime 값01.")
		private Date dateTimeValue01;

		@FieldDescribe("업무 데이터 DateTime 값02.")
		private Date dateTimeValue02;

		@FieldDescribe("업무 데이터 Date 값01.")
		private Date dateValue01;

		@FieldDescribe("업무 데이터 Date 값02.")
		private Date dateValue02;

		@FieldDescribe("업무 데이터 Time 값01.")
		private Date timeValue01;

		@FieldDescribe("업무 데이터 Time 값02.")
		private Date timeValue02;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getJob() {
			return job;
		}

		public void setJob(String job) {
			this.job = job;
		}

		public String getWork() {
			return work;
		}

		public void setWork(String work) {
			this.work = work;
		}

		public String getWorkCompleted() {
			return workCompleted;
		}

		public void setWorkCompleted(String workCompleted) {
			this.workCompleted = workCompleted;
		}

		public Boolean getCompleted() {
			return completed;
		}

		public void setCompleted(Boolean completed) {
			this.completed = completed;
		}

		public String getRead() {
			return read;
		}

		public void setRead(String read) {
			this.read = read;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getApplication() {
			return application;
		}

		public void setApplication(String application) {
			this.application = application;
		}

		public String getApplicationName() {
			return applicationName;
		}

		public void setApplicationName(String applicationName) {
			this.applicationName = applicationName;
		}

		public String getApplicationAlias() {
			return applicationAlias;
		}

		public void setApplicationAlias(String applicationAlias) {
			this.applicationAlias = applicationAlias;
		}

		public String getProcess() {
			return process;
		}

		public void setProcess(String process) {
			this.process = process;
		}

		public String getProcessName() {
			return processName;
		}

		public void setProcessName(String processName) {
			this.processName = processName;
		}

		public String getProcessAlias() {
			return processAlias;
		}

		public void setProcessAlias(String processAlias) {
			this.processAlias = processAlias;
		}

		public String getSerial() {
			return serial;
		}

		public void setSerial(String serial) {
			this.serial = serial;
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		public String getOpinion() {
			return opinion;
		}

		public void setOpinion(String opinion) {
			this.opinion = opinion;
		}

		public String getActivity() {
			return activity;
		}

		public void setActivity(String activity) {
			this.activity = activity;
		}

		public String getActivityName() {
			return activityName;
		}

		public void setActivityName(String activityName) {
			this.activityName = activityName;
		}

		public String getActivityAlias() {
			return activityAlias;
		}

		public void setActivityAlias(String activityAlias) {
			this.activityAlias = activityAlias;
		}

		public String getActivityDescription() {
			return activityDescription;
		}

		public void setActivityDescription(String activityDescription) {
			this.activityDescription = activityDescription;
		}

		public ActivityType getActivityType() {
			return activityType;
		}

		public void setActivityType(ActivityType activityType) {
			this.activityType = activityType;
		}

		public String getActivityToken() {
			return activityToken;
		}

		public void setActivityToken(String activityToken) {
			this.activityToken = activityToken;
		}

		public String getCreatorPerson() {
			return creatorPerson;
		}

		public void setCreatorPerson(String creatorPerson) {
			this.creatorPerson = creatorPerson;
		}

		public String getCreatorIdentity() {
			return creatorIdentity;
		}

		public void setCreatorIdentity(String creatorIdentity) {
			this.creatorIdentity = creatorIdentity;
		}

		public String getCreatorUnit() {
			return creatorUnit;
		}

		public void setCreatorUnit(String creatorUnit) {
			this.creatorUnit = creatorUnit;
		}

		public Date getStartTime() {
			return startTime;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

		public String getStartTimeMonth() {
			return startTimeMonth;
		}

		public void setStartTimeMonth(String startTimeMonth) {
			this.startTimeMonth = startTimeMonth;
		}

		public Date getCompletedTime() {
			return completedTime;
		}

		public void setCompletedTime(Date completedTime) {
			this.completedTime = completedTime;
		}

		public String getCompletedTimeMonth() {
			return completedTimeMonth;
		}

		public void setCompletedTimeMonth(String completedTimeMonth) {
			this.completedTimeMonth = completedTimeMonth;
		}

		public Long getDuration() {
			return duration;
		}

		public void setDuration(Long duration) {
			this.duration = duration;
		}

		public String getCurrentActivityName() {
			return currentActivityName;
		}

		public void setCurrentActivityName(String currentActivityName) {
			this.currentActivityName = currentActivityName;
		}

		public String getStringValue01() {
			return stringValue01;
		}

		public void setStringValue01(String stringValue01) {
			this.stringValue01 = stringValue01;
		}

		public String getStringValue02() {
			return stringValue02;
		}

		public void setStringValue02(String stringValue02) {
			this.stringValue02 = stringValue02;
		}

		public String getStringValue03() {
			return stringValue03;
		}

		public void setStringValue03(String stringValue03) {
			this.stringValue03 = stringValue03;
		}

		public String getStringValue04() {
			return stringValue04;
		}

		public void setStringValue04(String stringValue04) {
			this.stringValue04 = stringValue04;
		}

		public String getStringValue05() {
			return stringValue05;
		}

		public void setStringValue05(String stringValue05) {
			this.stringValue05 = stringValue05;
		}

		public String getStringValue06() {
			return stringValue06;
		}

		public void setStringValue06(String stringValue06) {
			this.stringValue06 = stringValue06;
		}

		public Boolean getBooleanValue01() {
			return booleanValue01;
		}

		public void setBooleanValue01(Boolean booleanValue01) {
			this.booleanValue01 = booleanValue01;
		}

		public Boolean getBooleanValue02() {
			return booleanValue02;
		}

		public void setBooleanValue02(Boolean booleanValue02) {
			this.booleanValue02 = booleanValue02;
		}

		public Double getDoubleValue01() {
			return doubleValue01;
		}

		public void setDoubleValue01(Double doubleValue01) {
			this.doubleValue01 = doubleValue01;
		}

		public Double getDoubleValue02() {
			return doubleValue02;
		}

		public void setDoubleValue02(Double doubleValue02) {
			this.doubleValue02 = doubleValue02;
		}

		public Long getLongValue01() {
			return longValue01;
		}

		public void setLongValue01(Long longValue01) {
			this.longValue01 = longValue01;
		}

		public Long getLongValue02() {
			return longValue02;
		}

		public void setLongValue02(Long longValue02) {
			this.longValue02 = longValue02;
		}

		public Date getDateTimeValue01() {
			return dateTimeValue01;
		}

		public void setDateTimeValue01(Date dateTimeValue01) {
			this.dateTimeValue01 = dateTimeValue01;
		}

		public Date getDateTimeValue02() {
			return dateTimeValue02;
		}

		public void setDateTimeValue02(Date dateTimeValue02) {
			this.dateTimeValue02 = dateTimeValue02;
		}

		public Date getDateValue01() {
			return dateValue01;
		}

		public void setDateValue01(Date dateValue01) {
			this.dateValue01 = dateValue01;
		}

		public Date getDateValue02() {
			return dateValue02;
		}

		public void setDateValue02(Date dateValue02) {
			this.dateValue02 = dateValue02;
		}

		public Date getTimeValue01() {
			return timeValue01;
		}

		public void setTimeValue01(Date timeValue01) {
			this.timeValue01 = timeValue01;
		}

		public Date getTimeValue02() {
			return timeValue02;
		}

		public void setTimeValue02(Date timeValue02) {
			this.timeValue02 = timeValue02;
		}
	}

}
