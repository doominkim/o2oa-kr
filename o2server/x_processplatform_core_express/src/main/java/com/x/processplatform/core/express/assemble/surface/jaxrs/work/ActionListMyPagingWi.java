package com.x.processplatform.core.express.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionListMyPagingWi extends GsonPropertyObject {

	private static final long serialVersionUID = -4432862273663133127L;

	@FieldDescribe("앱 식별자.")
	@Schema(description = "앱 식별자.")
	private List<String> applicationList = new ArrayList<>();

	@FieldDescribe("프로세스 식별자.")
	@Schema(description = "프로세스 식별자.")
	private List<String> processList = new ArrayList<>();

	@FieldDescribe("동일 버전 프로세스 데이터 조회 여부: true(기본 조회)|false")
	@Schema(description = "동일 버전 프로세스 데이터 조회 여부: true(기본 조회)|false")
	private Boolean relateEditionProcess = true;

	@FieldDescribe("시작 시간, 형식: yyyy-MM-dd HH:mm:ss")
	@Schema(description = "시작 시간, 형식: yyyy-MM-dd HH:mm:ss")
	private String startTime;

	@FieldDescribe("종료 시간, 형식: yyyy-MM-dd HH:mm:ss")
	@Schema(description = "종료 시간, 형식: yyyy-MM-dd HH:mm:ss")
	private String endTime;

	@FieldDescribe("시작 월, 형식: yyyy-MM.")
	@Schema(description = "시작 월, 형식: yyyy-MM.")
	private List<String> startTimeMonthList = new ArrayList<>();

	@FieldDescribe("활동 이름.")
	@Schema(description = "활동 이름.")
	private List<String> activityNameList = new ArrayList<>();

	@FieldDescribe("수동 노드 통과 여부, 초안 여부 판단에 사용. 도착 단계에서 판단.")
	@Schema(description = "수동 노드 통과 여부, 초안 여부 판단에 사용. 도착 단계에서 판단.")
	private Boolean workThroughManual;

	@FieldDescribe("현재 업무 문서의 저장 수정 작업 여부, 기본 생성 후 수정되지 않은 것인지 판단에 사용.")
	@Schema(description = "현재 업무 문서의 저장 수정 작업 여부, 기본 생성 후 수정되지 않은 것인지 판단에 사용.")
	private Boolean dataChanged;

	@FieldDescribe("키워드.")
	@Schema(description = "키워드.")
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

	public Boolean getWorkThroughManual() {
		return workThroughManual;
	}

	public void setWorkThroughManual(Boolean workThroughManual) {
		this.workThroughManual = workThroughManual;
	}

	public Boolean getDataChanged() {
		return dataChanged;
	}

	public void setDataChanged(Boolean dataChanged) {
		this.dataChanged = dataChanged;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}