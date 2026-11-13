package com.x.processplatform.core.express.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.bean.NameValueCountPair;
import com.x.base.core.project.gson.GsonPropertyObject;

public class ActionFilterAttributeWo extends GsonPropertyObject {

	private static final long serialVersionUID = 1206560608003793762L;

	@FieldDescribe("선택 가능한 프로세스")
	private List<NameValueCountPair> processList = new ArrayList<>();

	@FieldDescribe("선택 가능한 조직")
	private List<NameValueCountPair> creatorUnitList = new ArrayList<>();

	@FieldDescribe("선택 가능한 시작 월")
	private List<NameValueCountPair> startTimeMonthList = new ArrayList<>();

	@FieldDescribe("선택 가능한 활동 노드")
	private List<NameValueCountPair> activityNameList = new ArrayList<>();

	@FieldDescribe("선택 가능한 업무 문서 상태")
	@FieldTypeDescribe(fieldType = "enum", fieldValue = "start|processing|hanging", fieldTypeName = "com.x.processplatform.core.entity.content.WorkStatus")
	private List<NameValueCountPair> workStatusList = new ArrayList<>();

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

	public List<NameValueCountPair> getStartTimeMonthList() {
		return startTimeMonthList;
	}

	public void setStartTimeMonthList(List<NameValueCountPair> startTimeMonthList) {
		this.startTimeMonthList = startTimeMonthList;
	}

	public List<NameValueCountPair> getActivityNameList() {
		return activityNameList;
	}

	public void setActivityNameList(List<NameValueCountPair> activityNameList) {
		this.activityNameList = activityNameList;
	}

	public List<NameValueCountPair> getWorkStatusList() {
		return workStatusList;
	}

	public void setWorkStatusList(List<NameValueCountPair> workStatusList) {
		this.workStatusList = workStatusList;
	}

}