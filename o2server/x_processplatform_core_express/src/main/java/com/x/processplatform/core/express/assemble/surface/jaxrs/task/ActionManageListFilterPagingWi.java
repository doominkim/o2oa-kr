package com.x.processplatform.core.express.assemble.surface.jaxrs.task;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionManageListFilterPagingWi extends GsonPropertyObject {

	private static final long serialVersionUID = 3548558189684378598L;

	@FieldDescribe("앱 식별자.")
	@Schema(description = "앱 식별자.")
	private List<String> applicationList;

	@FieldDescribe("프로세스 식별자.")
	@Schema(description = "프로세스 식별자.")
	private List<String> processList;

	@FieldDescribe("같은 버전 프로세스 결재 대기 조회 여부: true(기본값, 조회)|false.")
	@Schema(description = "같은 버전 프로세스 결재 대기 조회 여부: true(기본값, 조회)|false.")
	private Boolean relateEditionProcess = true;

	@FieldDescribe("임시 저장 결재 대기 제외 여부: false(기본값, 제외 안 함)|true.")
	@Schema(description = "임시 저장 결재 대기 제외 여부: false(기본값, 제외 안 함)|true.")
	private Boolean isExcludeDraft;

	@FieldDescribe("시작 시간, 형식:yyyy-MM-dd HH:mm:ss.")
	@Schema(description = "시작 시간, 형식:yyyy-MM-dd HH:mm:ss.")
	private String startTime;

	@FieldDescribe("종료 시간, 형식:yyyy-MM-dd HH:mm:ss.")
	@Schema(description = "종료 시간, 형식:yyyy-MM-dd HH:mm:ss.")
	private String endTime;

	@FieldDescribe("사용자 식별자.")
	@Schema(description = "사용자 식별자.")
	private List<String> credentialList;

	@FieldDescribe("활동 이름.")
	@Schema(description = "활동 이름.")
	private List<String> activityNameList;

	@FieldDescribe("업무 문서 생성 신분의 소속 조직.")
	@Schema(description = "업무 문서 생성 신분의 소속 조직.")
	private List<String> creatorUnitList;

	@FieldDescribe("업무 문서 식별자.")
	@Schema(description = "업무 문서 식별자.")
	private List<String> workList;

	@FieldDescribe("작업 식별자.")
	@Schema(description = "작업 식별자.")
	private List<String> jobList;

	@FieldDescribe("시작 연월, 텍스트 형식 yyyy-MM")
	@Schema(description = "시작 연월, 텍스트 형식 yyyy-MM")
	private List<String> startTimeMonthList;

	@FieldDescribe("기한 초과 시간(0은 기한이 초과된 전체, 1은 1시간 이상 초과, 2, 3...)")
	@Schema(description = "기한 초과 시간(0은 기한이 초과된 전체, 1은 1시간 이상 초과, 2, 3...)")
	private String expireTime;

	@FieldDescribe("독촉 초과 시간(0은 기한이 초과된 전체, 1은 1시간 이상 초과, 2, 3...)")
	@Schema(description = "독촉 초과 시간(0은 기한이 초과된 전체, 1은 1시간 이상 초과, 2, 3...)")
	private String urgeTime;

	@FieldDescribe("검색 키워드, 검색 범위:제목, 의견, 문서번호, 생성자, 생성 부서.")
	@Schema(description = "검색 키워드, 검색 범위:제목, 의견, 문서번호, 생성자, 생성 부서.")
	private String key;

	@FieldDescribe("업무 데이터 String 값01.")
	@Schema(description = "업무 데이터 String 값01.")
	private String stringValue01;
	@FieldDescribe("업무 데이터 String 값02")
	@Schema(description = "업무 데이터 String 값02.")
	private String stringValue02;
	@FieldDescribe("업무 데이터 String 값03")
	@Schema(description = "업무 데이터 String 값03.")
	private String stringValue03;
	@FieldDescribe("업무 데이터 String 값04")
	@Schema(description = "업무 데이터 String 값04.")
	private String stringValue04;
	@FieldDescribe("업무 데이터 String 값05")
	@Schema(description = "업무 데이터 String 값05.")
	private String stringValue05;
	@FieldDescribe("업무 데이터 String 값06")
	@Schema(description = "업무 데이터 String 값06.")
	private String stringValue06;
	@FieldDescribe("업무 데이터 String 값07")
	@Schema(description = "업무 데이터 String 값07.")
	private String stringValue07;
	@FieldDescribe("업무 데이터 String 값08")
	@Schema(description = "업무 데이터 String 값08.")
	private String stringValue08;
	@FieldDescribe("업무 데이터 String 값09")
	@Schema(description = "업무 데이터 String 값09.")
	private String stringValue09;
	@FieldDescribe("업무 데이터 String 값10")
	@Schema(description = "업무 데이터 String 값10.")
	private String stringValue10;

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

	public List<String> getJobList() {
		return ListTools.trim(jobList, true, true);
	}

	public List<String> getWorkList() {
		return ListTools.trim(workList, true, true);
	}

	public List<String> getCredentialList() {
		return ListTools.trim(credentialList, true, true);
	}

	public List<String> getCreatorUnitList() {
		return ListTools.trim(creatorUnitList, true, true);
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

	public void setApplicationList(List<String> applicationList) {
		this.applicationList = applicationList;
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

	public void setStartTimeMonthList(List<String> startTimeMonthList) {
		this.startTimeMonthList = startTimeMonthList;
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

	public void setCreatorUnitList(List<String> creatorUnitList) {
		this.creatorUnitList = creatorUnitList;
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

	public String getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(String expireTime) {
		this.expireTime = expireTime;
	}

	public String getUrgeTime() {
		return urgeTime;
	}

	public void setUrgeTime(String urgeTime) {
		this.urgeTime = urgeTime;
	}

	public void setWorkList(List<String> workList) {
		this.workList = workList;
	}

	public void setJobList(List<String> jobList) {
		this.jobList = jobList;
	}

	public Boolean getExcludeDraft() {
		return isExcludeDraft;
	}

	public void setExcludeDraft(Boolean excludeDraft) {
		isExcludeDraft = excludeDraft;
	}
}