package com.x.processplatform.core.express.assemble.surface.jaxrs.read;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionFilterAttributeFilterWi extends GsonPropertyObject {

	private static final long serialVersionUID = 5251851744857095852L;

	@FieldDescribe("앱 범위 제한.")
	@Schema(description = "앱 범위 제한.")
	private List<String> applicationList = new ArrayList<>();

	@FieldDescribe("프로세스 범위 제한.")
	@Schema(description = "프로세스 범위 제한.")
	private List<String> processList = new ArrayList<>();

	@FieldDescribe("생성 조직 범위 제한.")
	@Schema(description = "생성 조직 범위 제한.")
	private List<String> creatorUnitList = new ArrayList<>();

	@FieldDescribe("생성 월 범위 제한.")
	@Schema(description = "생성 월 범위 제한.")
	private List<String> startTimeMonthList = new ArrayList<>();

	@FieldDescribe("활동 이름 범위 제한.")
	@Schema(description = "활동 이름 범위 제한.")
	private List<String> activityNameList = new ArrayList<>();

	@FieldDescribe("선택 가능한 완료 상태.")
	@Schema(description = "선택 가능한 완료 상태.")
	private List<Boolean> completedList = new ArrayList<>();

	public List<Boolean> getCompletedList() {
		return completedList;
	}

	public void setCompletedList(List<Boolean> completedList) {
		this.completedList = completedList;
	}

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

	public List<String> getCreatorUnitList() {
		return creatorUnitList;
	}

	public void setCreatorUnitList(List<String> creatorUnitList) {
		this.creatorUnitList = creatorUnitList;
	}

}