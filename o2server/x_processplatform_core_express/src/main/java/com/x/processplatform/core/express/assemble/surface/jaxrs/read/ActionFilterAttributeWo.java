package com.x.processplatform.core.express.assemble.surface.jaxrs.read;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.NameValueCountPair;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionFilterAttributeWo extends GsonPropertyObject {

	private static final long serialVersionUID = -7846051029815234834L;
	
	@FieldDescribe("선택 가능한 앱 범위.")
	@Schema(description = "선택 가능한 앱 범위.")
	private List<NameValueCountPair> applicationList = new ArrayList<>();
	@FieldDescribe("선택 가능한 프로세스 범위.")
	@Schema(description = "선택 가능한 프로세스 범위.")
	private List<NameValueCountPair> processList = new ArrayList<>();
	@FieldDescribe("선택 가능한 조직 범위.")
	@Schema(description = "선택 가능한 조직 범위.")
	private List<NameValueCountPair> creatorUnitList = new ArrayList<>();
	@FieldDescribe("선택 가능한 시작 월.")
	@Schema(description = "선택 가능한 시작 월.")
	private List<NameValueCountPair> startTimeMonthList = new ArrayList<>();
	@FieldDescribe("선택 가능한 활동 범위.")
	@Schema(description = "선택 가능한 활동 범위.")
	private List<NameValueCountPair> activityNameList = new ArrayList<>();
	@FieldDescribe("선택 가능한 완료 상태.")
	@Schema(description = "선택 가능한 완료 상태.")
	private List<NameValueCountPair> completedList = new ArrayList<>();

	public List<NameValueCountPair> getApplicationList() {
		return applicationList;
	}

	public void setApplicationList(List<NameValueCountPair> applicationList) {
		this.applicationList = applicationList;
	}

	public List<NameValueCountPair> getProcessList() {
		return processList;
	}

	public void setProcessList(List<NameValueCountPair> processList) {
		this.processList = processList;
	}

	public List<NameValueCountPair> getCreatorUnitList() {
		return creatorUnitList;
	}

	public void setCreatorUnitList(List<NameValueCountPair> creatorUnitList) {
		this.creatorUnitList = creatorUnitList;
	}

	public List<NameValueCountPair> getActivityNameList() {
		return activityNameList;
	}

	public void setActivityNameList(List<NameValueCountPair> activityNameList) {
		this.activityNameList = activityNameList;
	}

	public List<NameValueCountPair> getStartTimeMonthList() {
		return startTimeMonthList;
	}

	public void setStartTimeMonthList(List<NameValueCountPair> startTimeMonthList) {
		this.startTimeMonthList = startTimeMonthList;
	}

	public List<NameValueCountPair> getCompletedList() {
		return completedList;
	}

	public void setCompletedList(List<NameValueCountPair> completedList) {
		this.completedList = completedList;
	}

}