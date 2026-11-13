package com.x.processplatform.core.express.assemble.surface.jaxrs.readcompleted;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionListNextFilterWi extends GsonPropertyObject {

	private static final long serialVersionUID = -4097088510809374338L;

	@FieldDescribe("앱 식별자.")
	@Schema(description = "앱 식별자.")
	private List<String> applicationList;

	@FieldDescribe("프로세스 식별자.")
	@Schema(description = "프로세스 식별자.")
	private List<String> processList;

	@FieldDescribe("동일 버전 프로세스 데이터 조회 여부: true(기본 조회)|false.")
	@Schema(description = "동일 버전 프로세스 데이터 조회 여부: true(기본 조회)|false.")
	private Boolean relateEditionProcess = true;

	@FieldDescribe("생성자 소속 조직.")
	@Schema(description = "생성자 소속 조직.")
	private List<String> creatorUnitList;

	@FieldDescribe("시작 시간 월.")
	@Schema(description = "시작 시간 월.")
	private List<String> startTimeMonthList;

	@FieldDescribe("종료 시간 월.")
	@Schema(description = "종료 시간 월.")
	private List<String> completedTimeMonthList;

	@FieldDescribe("활동.")
	@Schema(description = "활동.")
	private List<String> activityNameList;

	@FieldDescribe("일치 키워드.")
	@Schema(description = "일치 키워드.")
	private String key;

	public List<String> getApplicationList() {
		return applicationList;
	}

	public void setApplicationList(List<String> applicationList) {
		this.applicationList = applicationList;
	}

	public List<String> getProcessList() {
		return processList;
	}

	public void setProcessList(List<String> processList) {
		this.processList = processList;
	}

	public Boolean getRelateEditionProcess() {
		return relateEditionProcess;
	}

	public void setRelateEditionProcess(Boolean relateEditionProcess) {
		this.relateEditionProcess = relateEditionProcess;
	}

	public List<String> getActivityNameList() {
		return activityNameList;
	}

	public void setActivityNameList(List<String> activityNameList) {
		this.activityNameList = activityNameList;
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

	public List<String> getCompletedTimeMonthList() {
		return completedTimeMonthList;
	}

	public void setCompletedTimeMonthList(List<String> completedTimeMonthList) {
		this.completedTimeMonthList = completedTimeMonthList;
	}

	public List<String> getStartTimeMonthList() {
		return startTimeMonthList;
	}

	public void setStartTimeMonthList(List<String> startTimeMonthList) {
		this.startTimeMonthList = startTimeMonthList;
	}

}