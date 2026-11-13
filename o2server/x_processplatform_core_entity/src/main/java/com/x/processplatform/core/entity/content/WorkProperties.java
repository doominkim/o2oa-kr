package com.x.processplatform.core.entity.content;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.processplatform.ManualTaskIdentityMatrix;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.ticket.Tickets;

public class WorkProperties extends JsonProperties {

	private static final long serialVersionUID = -62236689373222398L;

	@Deprecated(since = "8.2", forRemoval = true)
	@FieldDescribe("강제 결재 대기 처리자")
	private List<String> manualForceTaskIdentityList = new ArrayList<>();

	@Deprecated(since = "8.2", forRemoval = true)
	@FieldDescribe("위임 대상")
	private Map<String, String> manualEmpowerMap = new LinkedHashMap<>();

	@FieldDescribe("서비스 콜백 값")
	private Map<String, Object> serviceValue = new LinkedHashMap<>();

	@FieldDescribe("제목")
	private String title;

	@FieldDescribe("하위 프로세스 호출 후 하위 프로세스가 종료되면 회신 기록, 하위 프로세스 종료 여부 표시.")
	private String embedCompleted;

	@FieldDescribe("상위 업무 문서, 현재 업무 문서가 하위 프로세스 호출로 생성된 경우.")
	private String parentWork;

	@FieldDescribe("상위 업무 문서 Job, 현재 업무 문서가 하위 프로세스 호출로 생성된 경우.")
	private String parentJob;

	@FieldDescribe("Embed 활동이 생성한 Work의 Job.")
	private String embedTargetJob;

	@FieldDescribe("분할 값 목록")
	private List<String> splitValueList = new ArrayList<>();

	@Deprecated(since = "8.2", forRemoval = true)
	@FieldDescribe("결재 대기 신분 매트릭스")
	private ManualTaskIdentityMatrix manualTaskIdentityMatrix = new ManualTaskIdentityMatrix();

	@FieldDescribe("결재 대기 신분 매트릭스")
	private GoBackStore goBackStore;

	@FieldDescribe("goBack으로 이동 반려 시 사용.")
	private String goBackActivityToken;

	@FieldDescribe("분할 값 저장 객체.")
	private Map<String, String> splitTokenValueMap = new LinkedHashMap<>();

	@FieldDescribe("결재 대기 증빙.")
	private Tickets tickets;

	@FieldDescribe("강제 경로.")
	private Boolean forceRouteEnable;

	@FieldDescribe("현재 처리자 신분 병합 텍스트, ','로 구분, 이 필드는 현재 업무 문서의 처리자 표시에만 사용, 인덱스 없음.")
	private String manualTaskIdentityText;

	public String getManualTaskIdentityText() {
		return manualTaskIdentityText;
	}

	public void setManualTaskIdentityText(String manualTaskIdentityText) {
		this.manualTaskIdentityText = manualTaskIdentityText;
	}

	public Boolean getForceRouteEnable() {
		return forceRouteEnable;
	}

	public void setForceRouteEnable(Boolean forceRouteEnable) {
		this.forceRouteEnable = forceRouteEnable;
	}

	public Tickets getTickets() {
		return tickets;
	}

	public void setTickets(Tickets tickets) {
		this.tickets = tickets;
	}

	public Map<String, String> getSplitTokenValueMap() {
		return splitTokenValueMap;
	}

	public void setSplitTokenValueMap(Map<String, String> splitTokenValueMap) {
		this.splitTokenValueMap = splitTokenValueMap;
	}

	public String getGoBackActivityToken() {
		return goBackActivityToken;
	}

	public void setGoBackActivityToken(String goBackActivityToken) {
		this.goBackActivityToken = goBackActivityToken;
	}

	public GoBackStore getGoBackStore() {
		return goBackStore;
	}

	public void setGoBackStore(GoBackStore goBackStore) {
		this.goBackStore = goBackStore;
	}

	public ManualTaskIdentityMatrix getManualTaskIdentityMatrix() {
		return manualTaskIdentityMatrix;
	}

	public void setManualTaskIdentityMatrix(ManualTaskIdentityMatrix manualTaskIdentityMatrix) {
		this.manualTaskIdentityMatrix = manualTaskIdentityMatrix;
	}

	public List<String> getManualForceTaskIdentityList() {
		if (this.manualForceTaskIdentityList == null) {
			this.manualForceTaskIdentityList = new ArrayList<>();
		}
		return this.manualForceTaskIdentityList;
	}

	public void setManualForceTaskIdentityList(List<String> manualForceTaskIdentityList) {
		this.manualForceTaskIdentityList = manualForceTaskIdentityList;
	}

	public Map<String, String> getManualEmpowerMap() {
		if (this.manualEmpowerMap == null) {
			this.manualEmpowerMap = new LinkedHashMap<>();
		}
		return this.manualEmpowerMap;
	}

	public void setManualEmpowerMap(Map<String, String> manualEmpowerMap) {
		this.manualEmpowerMap = manualEmpowerMap;
	}

	public Map<String, Object> getServiceValue() {
		if (this.serviceValue == null) {
			this.serviceValue = new LinkedHashMap<>();
		}
		return this.serviceValue;
	}

	public void setServiceValue(Map<String, Object> serviceValue) {
		this.serviceValue = serviceValue;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getSplitValueList() {
		if (null == this.splitValueList) {
			this.splitValueList = new ArrayList<>();
		}
		return this.splitValueList;
	}

	public void setSplitValueList(List<String> splitValueList) {
		this.splitValueList = splitValueList;
	}

	public String getEmbedCompleted() {
		return embedCompleted;
	}

	public void setEmbedCompleted(String embedCompleted) {
		this.embedCompleted = embedCompleted;
	}

	public String getParentWork() {
		return parentWork;
	}

	public void setParentWork(String parentWork) {
		this.parentWork = parentWork;
	}

	public String getParentJob() {
		return parentJob;
	}

	public void setParentJob(String parentJob) {
		this.parentJob = parentJob;
	}

	public String getEmbedTargetJob() {
		return embedTargetJob;
	}

	public void setEmbedTargetJob(String embedTargetJob) {
		this.embedTargetJob = embedTargetJob;
	}

	/**
	 * work에 저장된 회송 작업에 필요한 데이터
	 * 
	 * @author ray
	 *
	 */
	public static class GoBackStore extends GsonPropertyObject {

		private static final long serialVersionUID = 2334994291846390241L;

		@Deprecated(since = "8.2", forRemoval = true)
		private ManualTaskIdentityMatrix manualTaskIdentityMatrix = new ManualTaskIdentityMatrix();
		private Tickets tickets;
		private String activity;
		private ActivityType activityType;
		private String activityToken;

		public ManualTaskIdentityMatrix getManualTaskIdentityMatrix() {
			return manualTaskIdentityMatrix;
		}

		public void setManualTaskIdentityMatrix(ManualTaskIdentityMatrix manualTaskIdentityMatrix) {
			this.manualTaskIdentityMatrix = manualTaskIdentityMatrix;
		}

		public String getActivityToken() {
			return activityToken;
		}

		public void setActivityToken(String activityToken) {
			this.activityToken = activityToken;
		}

		public Tickets getTickets() {
			return tickets;
		}

		public void setTickets(Tickets tickets) {
			this.tickets = tickets;
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

	}

}
