package com.x.processplatform.core.entity.content;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.processplatform.core.entity.element.ActivityType;

public class TaskProperties extends JsonProperties {

	private static final long serialVersionUID = 5628694071505848771L;

	@FieldDescribe("이전 수동 단계 처리자.")
	private String prevTaskIdentity;

	@FieldDescribe("이전 수동 단계 처리자 목록.")
	private List<String> prevTaskIdentityList;

	@FieldDescribe("제목.")
	private String title;

	@FieldDescribe("의견.")
	private String opinion;

	@FieldDescribe("이전 수동 단계 결재 대기 객체 목록.")
	private List<PrevTask> prevTaskList;

	@FieldDescribe("이전 수동 단계 최근 결재 대기 객체.")
	private PrevTask prevTask;

	@FieldDescribe("결재 대기 시간 측정 일시 중지 근무 시간 길이(분).")
	private Date pauseStartTime;

	@FieldDescribe("결재 대기 시간 측정 일시 중지 근무 시간 길이(분).")
	private Integer pauseWorkTimeMinutes;

	@FieldDescribe("결재 대기 시간 측정 일시 중지 자연 시간 길이(분).")
	private Integer pauseMinutes;

	@FieldDescribe("결재 대기의 routeName 비활성 여부, 반려 결재 대기에서 way=jump로 설정하면 바로 이동하므로 routeName 불필요.")
	private Boolean routeNameDisable;

	@FieldDescribe("Ticket 생성 방식, create, reset, add.")
	private String act;

	@FieldDescribe("작업자, 추가 결재에서 추가 결재를 실행한 사람 기록")
	private String actPerson;

	public String getActPerson() {
		return actPerson;
	}

	public void setActPerson(String actPerson) {
		this.actPerson = actPerson;
	}
	
	public String getAct() {
		return act;
	}

	public void setAct(String act) {
		this.act = act;
	}

	public Boolean getRouteNameDisable() {
		return routeNameDisable;
	}

	public void setRouteNameDisable(Boolean routeNameDisable) {
		this.routeNameDisable = routeNameDisable;
	}

	public List<String> getPrevTaskIdentityList() {
		if (null == prevTaskIdentityList) {
			this.prevTaskIdentityList = new ArrayList<String>();
		}
		return prevTaskIdentityList;
	}

	public List<PrevTask> getPrevTaskList() {
		if (null == prevTaskList) {
			this.prevTaskList = new ArrayList<PrevTask>();
		}
		return prevTaskList;
	}

	public void setPrevTaskIdentityList(List<String> prevTaskIdentityList) {
		this.prevTaskIdentityList = prevTaskIdentityList;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public String getPrevTaskIdentity() {
		return prevTaskIdentity;
	}

	public void setPrevTaskIdentity(String prevTaskIdentity) {
		this.prevTaskIdentity = prevTaskIdentity;
	}

	public static class PrevTask extends GsonPropertyObject {

		private static final long serialVersionUID = 7339649206771167172L;

		private String routeName;
		private String unit;
		private String identity;
		private String person;
		private String opinion;
		private Date startTime;
		private Date completedTime;
		private String activity;
		private String activityToken;
		private String activityName;
		private ActivityType activityType;

		public ActivityType getActivityType() {
			return activityType;
		}

		public void setActivityType(ActivityType activityType) {
			this.activityType = activityType;
		}

		public String getActivity() {
			return activity;
		}

		public void setActivity(String activity) {
			this.activity = activity;
		}

		public String getActivityToken() {
			return activityToken;
		}

		public void setActivityToken(String activityToken) {
			this.activityToken = activityToken;
		}

		public String getActivityName() {
			return activityName;
		}

		public void setActivityName(String activityName) {
			this.activityName = activityName;
		}

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

		public String getOpinion() {
			return opinion;
		}

		public void setOpinion(String opinion) {
			this.opinion = opinion;
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public Date getStartTime() {
			return startTime;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

		public Date getCompletedTime() {
			return completedTime;
		}

		public void setCompletedTime(Date completedTime) {
			this.completedTime = completedTime;
		}

		public String getRouteName() {
			return routeName;
		}

		public void setRouteName(String routeName) {
			this.routeName = routeName;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

	}

	public void setPrevTaskList(List<PrevTask> prevTaskList) {
		this.prevTaskList = prevTaskList;
	}

	public PrevTask getPrevTask() {
		return prevTask;
	}

	public void setPrevTask(PrevTask prevTask) {
		this.prevTask = prevTask;
	}

	public Integer getPauseWorkTimeMinutes() {
		return pauseWorkTimeMinutes;
	}

	public void setPauseWorkTimeMinutes(Integer pauseWorkTimeMinutes) {
		this.pauseWorkTimeMinutes = pauseWorkTimeMinutes;
	}

	public Integer getPauseMinutes() {
		return pauseMinutes;
	}

	public void setPauseMinutes(Integer pauseMinutes) {
		this.pauseMinutes = pauseMinutes;
	}

	public Date getPauseStartTime() {
		return pauseStartTime;
	}

	public void setPauseStartTime(Date pauseStartTime) {
		this.pauseStartTime = pauseStartTime;
	}

}