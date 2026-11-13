package com.x.processplatform.core.express.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.processplatform.core.entity.element.ActivityType;

public class V2RerouteWi extends GsonPropertyObject {

	private static final long serialVersionUID = -6729193803512068864L;

	@FieldDescribe("대상 노드.")
	private String activity;

	@FieldDescribe("노드 유형.")
	private ActivityType activityType;

	@FieldDescribe("모든 work 병합 여부")
	private Boolean mergeWork;

	@FieldDescribe("수동 활동 강제 처리자")
	private List<String> distinguishedNameList = new ArrayList<>();

	@FieldDescribe("수동 활동 강제 처리자, 구버전 호환.")
	private List<String> manualForceTaskIdentityList = new ArrayList<>();

	@FieldDescribe("결재 대기 선택 경로 이름.")
	private String routeName;

	@FieldDescribe("배정자, 기본값은 현재 로그인 사용자.")
	private String operator;

	@FieldDescribe("결재 대기 처리 의견.")
	private String opinion;

	public List<String> getManualForceTaskIdentityList() {
		return manualForceTaskIdentityList;
	}

	public void setManualForceTaskIdentityList(List<String> manualForceTaskIdentityList) {
		this.manualForceTaskIdentityList = manualForceTaskIdentityList;
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

	public Boolean getMergeWork() {
		return BooleanUtils.isTrue(mergeWork);
	}

	public List<String> getDistinguishedNameList() {
		if (null == distinguishedNameList) {
			this.distinguishedNameList = new ArrayList<>();
		}
		return this.distinguishedNameList;
	}

	public void setDistinguishedNameList(List<String> distinguishedNameList) {
		this.distinguishedNameList = distinguishedNameList;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public void setMergeWork(Boolean mergeWork) {
		this.mergeWork = mergeWork;
	}

	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
}
