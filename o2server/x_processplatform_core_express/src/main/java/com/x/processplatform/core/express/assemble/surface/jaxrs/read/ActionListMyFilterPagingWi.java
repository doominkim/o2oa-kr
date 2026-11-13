package com.x.processplatform.core.express.assemble.surface.jaxrs.read;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionListMyFilterPagingWi extends GsonPropertyObject {

	private static final long serialVersionUID = -7285242594787321737L;

	@FieldDescribe("앱 식별자.")
	@Schema(description = "앱 식별자.")
	private List<String> applicationList;

	@FieldDescribe("프로세스 식별자.")
	@Schema(description = "프로세스 식별자.")
	private List<String> processList;

	@FieldDescribe("동일 버전 프로세스 데이터 조회 여부: true(기본 조회)|false.")
	@Schema(description = "동일 버전 프로세스 데이터 조회 여부: true(기본 조회)|false.")
	private Boolean relateEditionProcess = true;

	@FieldDescribe("시작 시간, 형식:yyyy-MM-dd HH:mm:ss.")
	@Schema(description = "시작 시간, 형식:yyyy-MM-dd HH:mm:ss.")
	private String startTime;

	@FieldDescribe("종료 시간, 형식:yyyy-MM-dd HH:mm:ss.")
	@Schema(description = "종료 시간, 형식:yyyy-MM-dd HH:mm:ss.")
	private String endTime;

	@FieldDescribe("생성 신분 소속 조직.")
	@Schema(description = "생성 신분 소속 조직.")
	private List<String> creatorUnitList;

	@FieldDescribe("열람 대기 시작 시간.")
	@Schema(description = "열람 대기 시작 시간.")
	private List<String> startTimeMonthList;

	@FieldDescribe("활동 이름.")
	@Schema(description = "활동 이름.")
	private List<String> activityNameList;

	@FieldDescribe("검색 키워드.")
	@Schema(description = "검색 키워드.")
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

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

}