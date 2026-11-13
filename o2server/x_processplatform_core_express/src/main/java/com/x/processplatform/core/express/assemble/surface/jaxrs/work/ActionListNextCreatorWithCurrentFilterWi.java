package com.x.processplatform.core.express.assemble.surface.jaxrs.work;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.processplatform.core.entity.content.WorkStatus;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionListNextCreatorWithCurrentFilterWi extends GsonPropertyObject {

	private static final long serialVersionUID = -5667997938739822509L;

	@FieldDescribe("프로세스 식별자.")
	@Schema(description = "프로세스 식별자.")
	private List<String> processList;

	@FieldDescribe("업무 문서 생성 사용자 신분 소속 조직.")
	@Schema(description = "업무 문서 생성 사용자 신분 소속 조직.")
	private List<String> creatorUnitList;

	@FieldDescribe("업무 문서 생성 시작 월, 형식: yyyy-MM.")
	@Schema(description = "업무 문서 생성 시작 월, 형식: yyyy-MM.")
	private List<String> startTimeMonthList;

	@FieldDescribe("활동 이름.")
	@Schema(description = "활동 이름.")
	private List<String> activityNameList;

	@FieldDescribe("업무 문서 상태.")
	@Schema(description = "업무 문서 상태.")
	private List<WorkStatus> workStatusList;

	@FieldDescribe("키워드.")
	@Schema(description = "키워드.")
	private String key;

	public List<String> getProcessList() {
		return processList;
	}

	public void setProcessList(List<String> processList) {
		this.processList = processList;
	}

	public List<String> getStartTimeMonthList() {
		return startTimeMonthList;
	}

	public void setStartTimeMonthList(List<String> startTimeMonthList) {
		this.startTimeMonthList = startTimeMonthList;
	}

	public List<String> getActivityNameList() {
		return activityNameList;
	}

	public void setActivityNameList(List<String> activityNameList) {
		this.activityNameList = activityNameList;
	}

	public List<WorkStatus> getWorkStatusList() {
		return workStatusList;
	}

	public void setWorkStatusList(List<WorkStatus> workStatusList) {
		this.workStatusList = workStatusList;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<String> getCreatorUnitList() {
		return creatorUnitList;
	}

	public void setCreatorUnitList(List<String> creatorUnitList) {
		this.creatorUnitList = creatorUnitList;
	}

}