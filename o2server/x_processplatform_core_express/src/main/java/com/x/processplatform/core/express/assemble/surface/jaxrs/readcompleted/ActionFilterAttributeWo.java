package com.x.processplatform.core.express.assemble.surface.jaxrs.readcompleted;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.NameValueCountPair;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionFilterAttributeWo extends GsonPropertyObject {

	private static final long serialVersionUID = 1632781666272985480L;

	@FieldDescribe("선택 가능한 앱 식별자.")
	@Schema(description = "선택 가능한 앱 식별자.")
	private List<NameValueCountPair> applicationList = new ArrayList<>();
	@FieldDescribe("선택 가능한 프로세스 식별자.")
	@Schema(description = "선택 가능한 프로세스 식별자.")
	private List<NameValueCountPair> processList = new ArrayList<>();
	@FieldDescribe("선택 가능한 조직 식별자.")
	@Schema(description = "선택 가능한 조직 식별자.")
	private List<NameValueCountPair> creatorUnitList = new ArrayList<>();
	@FieldDescribe("선택 가능한 생성 시간 범위.")
	@Schema(description = "선택 가능한 생성 시간 범위.")
	private List<NameValueCountPair> startTimeMonthList = new ArrayList<>();
	@FieldDescribe("선택 가능한 종료 시간 범위.")
	@Schema(description = "선택 가능한 종료 시간 범위.")
	private List<NameValueCountPair> completedTimeMonthList = new ArrayList<>();
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

	public List<NameValueCountPair> getCompletedTimeMonthList() {
		return completedTimeMonthList;
	}

	public void setCompletedTimeMonthList(List<NameValueCountPair> completedTimeMonthList) {
		this.completedTimeMonthList = completedTimeMonthList;
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