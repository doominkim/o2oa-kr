package com.x.processplatform.core.express.assemble.surface.jaxrs.task;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionFilterAttributeFilterWi extends GsonPropertyObject {

	private static final long serialVersionUID = -3260796933524976316L;

	@FieldDescribe("앱 범위 제한")
	@Schema(description = "앱 범위 제한 목록")
	private List<String> applicationList = new ArrayList<>();

	@FieldDescribe("프로세스 범위 제한")
	@Schema(description = "프로세스 범위 제한 목록")
	private List<String> processList = new ArrayList<>();

	@FieldDescribe("생성 조직 범위 제한")
	@Schema(description = "생성 조직 범위 제한 목록")
	private List<String> creatorUnitList = new ArrayList<>();

	@FieldDescribe("생성 월 범위 제한")
	@Schema(description = "생성 월 범위 제한 목록")
	private List<String> startTimeMonthList = new ArrayList<>();

	@FieldDescribe("활동 이름 범위 제한")
	@Schema(description = "활동 이름 범위 제한 목록")
	private List<String> activityNameList = new ArrayList<>();

	public List<String> getApplicationList() {
		return ListTools.trim(applicationList, true, true);
	}

	public List<String> getProcessList() {
		return ListTools.trim(processList, true, true);
	}

	public List<String> getStartTimeMonthList() {
		return ListTools.trim(startTimeMonthList, true, true);
	}

	public List<String> getActivityNameList() {
		return ListTools.trim(activityNameList, true, true);
	}

	public List<String> getCreatorUnitList() {
		return ListTools.trim(creatorUnitList, true, true);
	}

	public void setProcessList(List<String> processList) {
		this.processList = processList;
	}

	public void setStartTimeMonthList(List<String> startTimeMonthList) {
		this.startTimeMonthList = startTimeMonthList;
	}

	public void setActivityNameList(List<String> activityNameList) {
		this.activityNameList = activityNameList;
	}

	public void setCreatorUnitList(List<String> creatorUnitList) {
		this.creatorUnitList = creatorUnitList;
	}

	public void setApplicationList(List<String> applicationList) {
		this.applicationList = applicationList;
	}

}