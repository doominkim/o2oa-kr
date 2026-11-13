package com.x.processplatform.core.express.assemble.surface.jaxrs.read;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionManageListFilterPagingWi extends GsonPropertyObject {

	private static final long serialVersionUID = -952086343832090320L;

	@FieldDescribe("앱 식별자.")
	@Schema(description = "앱 식별자.")
	private List<String> applicationList;

	@FieldDescribe("프로세스 식별자.")
	@Schema(description = "프로세스 식별자.")
	private List<String> processList;

	@FieldDescribe("동일 버전 프로세스 데이터 조회 여부: true(기본 조회)|false.")
	@Schema(description = "동일 버전 프로세스 데이터 조회 여부: true(기본 조회)|false.")
	private Boolean relateEditionProcess = true;

	@FieldDescribe("시작 시간, 형식: yyyy-MM-dd HH:mm:ss")
	@Schema(description = "시작 시간, 형식: yyyy-MM-dd HH:mm:ss")
	private String startTime;

	@FieldDescribe("종료 시간, 형식: yyyy-MM-dd HH:mm:ss")
	@Schema(description = "종료 시간, 형식: yyyy-MM-dd HH:mm:ss")
	private String endTime;

	@FieldDescribe("사용자 식별자.")
	@Schema(description = "사용자 식별자.")
	private List<String> credentialList;

	@FieldDescribe("활동 이름.")
	@Schema(description = "활동 이름.")
	private List<String> activityNameList;

	@FieldDescribe("생성 신분 소속 조직.")
	@Schema(description = "생성 신분 소속 조직.")
	private List<String> creatorUnitList;

	@FieldDescribe("업무 문서 식별자.")
	@Schema(description = "업무 문서 식별자.")
	private List<String> workList;

	@FieldDescribe("작업 식별자.")
	@Schema(description = "작업 식별자.")
	private List<String> jobList;

	@FieldDescribe("시작 연월.")
	@Schema(description = "시작 연월.")
	private List<String> startTimeMonthList;

	@FieldDescribe("검색 키워드.")
	@Schema(description = "검색 키워드.")
	private String key;

	@FieldDescribe("현재 열람 대기자.")
	@Schema(description = "현재 열람 대기자.")
	private String person;

	@FieldDescribe("업무 데이터 String 값01.")
	@Schema(description = "업무 데이터 String 값01.")
	private String stringValue01;

	@FieldDescribe("업무 데이터 String 값02.")
	@Schema(description = "업무 데이터 String 값02.")
	private String stringValue02;

	@FieldDescribe("업무 데이터 String 값03.")
	@Schema(description = "업무 데이터 String 값03.")
	private String stringValue03;

	@FieldDescribe("업무 데이터 String 값04.")
	@Schema(description = "업무 데이터 String 값04.")
	private String stringValue04;

	@FieldDescribe("업무 데이터 String 값05.")
	@Schema(description = "업무 데이터 String 값05.")
	private String stringValue05;

	@FieldDescribe("업무 데이터 String 값06.")
	@Schema(description = "업무 데이터 String 값06.")
	private String stringValue06;

	@FieldDescribe("업무 데이터 String 값07.")
	@Schema(description = "업무 데이터 String 값07.")
	private String stringValue07;

	@FieldDescribe("업무 데이터 String 값08.")
	@Schema(description = "업무 데이터 String 값08.")
	private String stringValue08;

	@FieldDescribe("업무 데이터 String 값09.")
	@Schema(description = "업무 데이터 String 값09.")
	private String stringValue09;

	@FieldDescribe("업무 데이터 String 값10.")
	@Schema(description = "업무 데이터 String 값10.")
	private String stringValue10;

	public List<String> getApplicationList() {
		return applicationList;
	}

	public String getPerson() {
		return person;
	}

	public String getStringValue01() {
		return stringValue01;
	}

	public String getStringValue02() {
		return stringValue02;
	}

	public String getStringValue03() {
		return stringValue03;
	}

	public String getStringValue04() {
		return stringValue04;
	}

	public String getStringValue05() {
		return stringValue05;
	}

	public String getStringValue06() {
		return stringValue06;
	}

	public String getStringValue07() {
		return stringValue07;
	}

	public String getStringValue08() {
		return stringValue08;
	}

	public String getStringValue09() {
		return stringValue09;
	}

	public String getStringValue10() {
		return stringValue10;
	}

	public void setStringValue01(String stringValue01) {
		this.stringValue01 = stringValue01;
	}

	public void setStringValue02(String stringValue02) {
		this.stringValue02 = stringValue02;
	}

	public void setStringValue03(String stringValue03) {
		this.stringValue03 = stringValue03;
	}

	public void setStringValue04(String stringValue04) {
		this.stringValue04 = stringValue04;
	}

	public void setStringValue05(String stringValue05) {
		this.stringValue05 = stringValue05;
	}

	public void setStringValue06(String stringValue06) {
		this.stringValue06 = stringValue06;
	}

	public void setStringValue07(String stringValue07) {
		this.stringValue07 = stringValue07;
	}

	public void setStringValue08(String stringValue08) {
		this.stringValue08 = stringValue08;
	}

	public void setStringValue09(String stringValue09) {
		this.stringValue09 = stringValue09;
	}

	public void setStringValue10(String stringValue10) {
		this.stringValue10 = stringValue10;
	}

	public void setPerson(String person) {
		this.person = person;
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

	public List<String> getCredentialList() {
		return credentialList;
	}

	public void setCredentialList(List<String> credentialList) {
		this.credentialList = credentialList;
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

	public List<String> getWorkList() {
		return workList;
	}

	public void setWorkList(List<String> workList) {
		this.workList = workList;
	}

	public List<String> getJobList() {
		return jobList;
	}

	public void setJobList(List<String> jobList) {
		this.jobList = jobList;
	}
}