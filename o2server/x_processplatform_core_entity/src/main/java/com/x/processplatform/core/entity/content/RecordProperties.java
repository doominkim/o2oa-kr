package com.x.processplatform.core.entity.content;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.processplatform.core.entity.element.ActivityType;

public class RecordProperties extends JsonProperties {

	private static final long serialVersionUID = 4021727898101550914L;

	@FieldDescribe("record에 기록된 처리자 정렬 값.")
	private Integer personOrderNumber = null;

	@FieldDescribe("record에 기록된 처리자 신분 정렬 값.")
	private Integer identityOrderNumber = null;

	@FieldDescribe("record에 기록된 처리자 신분 소속 조직 정렬 값.")
	private Integer unitOrderNumber = null;

	@FieldDescribe("record에 기록된 처리자 신분 소속 조직 계층 이름.")
	private String unitLevelName = "";

	@FieldDescribe("record에 기록된 처리자 신분이 보유한 조직 직무, 상위로 재귀.")
	private List<String> unitDutyList = new ArrayList<>();

	@FieldDescribe("후속 수동 단계")
	private List<NextManual> nextManualList = new ArrayList<>();

	@FieldDescribe("후속 수동 단계 처리자")
	private List<String> nextManualTaskIdentityList = new ArrayList<>();

	@FieldDescribe("위임받은 처리자")
	private String empowerToPerson;

	@FieldDescribe("위임받은 처리자의 신분")
	private String empowerToIdentity;

	@FieldDescribe("위임 원 신분")
	private String empowerFromIdentity;

	@FieldDescribe("위임받은 처리자의 조직")
	private String empowerToUnit;

	@FieldDescribe("경로 이름")
	private String routeName;

	@FieldDescribe("의견")
	private String opinion;

	@FieldDescribe("멀티미디어 의견")
	private String mediaOpinion;

	@FieldDescribe("시작 시간.")
	private Date startTime;

	@FieldDescribe("소요 시간")
	private Long elapsed;

	@FieldDescribe("활동 그룹")
	private String fromGroup;

	@FieldDescribe("의견 그룹")
	private String fromOpinionGroup;

	@FieldDescribe("회수를 시작한 처리 완료 식별자")
	private String retractTaskCompleted;

	@FieldDescribe("처리 완료 식별자")
	private String taskCompleted;

	public String getTaskCompleted() {
		return taskCompleted;
	}

	public void setTaskCompleted(String taskCompleted) {
		this.taskCompleted = taskCompleted;
	}

	public String getRetractTaskCompleted() {
		return retractTaskCompleted;
	}

	public void setRetractTaskCompleted(String retractTaskCompleted) {
		this.retractTaskCompleted = retractTaskCompleted;
	}

	public Integer getPersonOrderNumber() {
		return personOrderNumber;
	}

	public void setPersonOrderNumber(Integer personOrderNumber) {
		this.personOrderNumber = personOrderNumber;
	}

	public List<String> getNextManualTaskIdentityList() {
		if (null == this.nextManualTaskIdentityList) {
			this.nextManualTaskIdentityList = new ArrayList<>();
		}
		return this.nextManualTaskIdentityList;
	}

	public void setNextManualTaskIdentityList(List<String> nextManualTaskIdentityList) {
		this.nextManualTaskIdentityList = nextManualTaskIdentityList;
	}

	public List<NextManual> getNextManualList() {
		if (null == this.nextManualList) {
			this.nextManualList = new ArrayList<>();
		}
		return this.nextManualList;
	}

	public static class NextManual {

		public NextManual() {
			// nothing
		}

		@FieldDescribe("활동")
		private String activity;
		@FieldDescribe("활동 유형")
		private ActivityType activityType;
		@FieldDescribe("활동 이름")
		private String activityName;
		@FieldDescribe("활동 별칭")
		private String activityAlias;
		@FieldDescribe("활동 토큰")
		private String activityToken;
		@FieldDescribe("결재 대기 처리자")
		private List<String> taskIdentityList = new ArrayList<>();

		public List<String> getTaskIdentityList() {
			if (null == this.taskIdentityList) {
				this.taskIdentityList = new ArrayList<>();
			}
			return this.taskIdentityList;
		}

		public String getActivity() {
			return activity;
		}

		public void setActivity(String activity) {
			this.activity = activity;
		}

		public ActivityType getActivityType() {
			return activityType;
		}

		public void setActivityType(ActivityType activityType) {
			this.activityType = activityType;
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

		public String getActivityToken() {
			return activityToken;
		}

		public void setActivityToken(String activityToken) {
			this.activityToken = activityToken;
		}

		public void setTaskIdentityList(List<String> identityList) {
			this.taskIdentityList = identityList;
		}

	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public String getMediaOpinion() {
		return mediaOpinion;
	}

	public void setMediaOpinion(String mediaOpinion) {
		this.mediaOpinion = mediaOpinion;
	}

	public void setNextManualList(List<NextManual> nextManualList) {
		this.nextManualList = nextManualList;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Long getElapsed() {
		return elapsed;
	}

	public void setElapsed(Long elapsed) {
		this.elapsed = elapsed;
	}

	public String getFromGroup() {
		return fromGroup;
	}

	public void setFromGroup(String fromGroup) {
		this.fromGroup = fromGroup;
	}

	public String getFromOpinionGroup() {
		return fromOpinionGroup;
	}

	public void setFromOpinionGroup(String fromOpinionGroup) {
		this.fromOpinionGroup = fromOpinionGroup;
	}

	public String getEmpowerToIdentity() {
		return empowerToIdentity;
	}

	public void setEmpowerToIdentity(String empowerToIdentity) {
		this.empowerToIdentity = empowerToIdentity;
	}

	public String getEmpowerToPerson() {
		return empowerToPerson;
	}

	public void setEmpowerToPerson(String empowerToPerson) {
		this.empowerToPerson = empowerToPerson;
	}

	public String getEmpowerToUnit() {
		return empowerToUnit;
	}

	public void setEmpowerToUnit(String empowerToUnit) {
		this.empowerToUnit = empowerToUnit;
	}

	public String getEmpowerFromIdentity() {
		return empowerFromIdentity;
	}

	public void setEmpowerFromIdentity(String empowerFromIdentity) {
		this.empowerFromIdentity = empowerFromIdentity;
	}

	public List<String> getUnitDutyList() {
		return unitDutyList;
	}

	public void setUnitDutyList(List<String> unitDutyList) {
		this.unitDutyList = unitDutyList;
	}

	public Integer getIdentityOrderNumber() {
		return identityOrderNumber;
	}

	public void setIdentityOrderNumber(Integer identityOrderNumber) {
		this.identityOrderNumber = identityOrderNumber;
	}

	public Integer getUnitOrderNumber() {
		return unitOrderNumber;
	}

	public void setUnitOrderNumber(Integer unitOrderNumber) {
		this.unitOrderNumber = unitOrderNumber;
	}

	public String getUnitLevelName() {
		return unitLevelName;
	}

	public void setUnitLevelName(String unitLevelName) {
		this.unitLevelName = unitLevelName;
	}

}
