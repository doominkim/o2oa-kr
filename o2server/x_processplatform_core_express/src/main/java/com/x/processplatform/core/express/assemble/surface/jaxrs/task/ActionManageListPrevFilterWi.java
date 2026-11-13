package com.x.processplatform.core.express.assemble.surface.jaxrs.task;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionManageListPrevFilterWi extends GsonPropertyObject {

	private static final long serialVersionUID = -3378981852416873278L;

	@FieldDescribe("앱 식별자.")
	@Schema(description = "앱 식별자.")
	private List<String> applicationList;

	@FieldDescribe("프로세스 식별자.")
	@Schema(description = "프로세스 식별자.")
	private List<String> processList;

	@FieldDescribe("사용자 식별자.")
	@Schema(description = "사용자 식별자.")
	private List<String> credentialList;

	@FieldDescribe("활동 이름.")
	@Schema(description = "활동 이름.")
	private List<String> activityNameList;

	@FieldDescribe("업무 문서 생성 신분의 소속 조직.")
	@Schema(description = "업무 문서 생성 신분의 소속 조직.")
	private List<String> creatorUnitList;

	@FieldDescribe("시작 연월, 텍스트 형식 yyyy-MM")
	@Schema(description = "시작 연월, 텍스트 형식 yyyy-MM")
	private List<String> startTimeMonthList;

	@FieldDescribe("검색 키워드, 검색 범위:제목, 의견, 문서번호, 생성자, 생성 부서.")
	@Schema(description = "검색 키워드, 검색 범위:제목, 의견, 문서번호, 생성자, 생성 부서.")
	private String key;

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

	public List<String> getCredentialList() {
		return ListTools.trim(credentialList, true, true);
	}

	public List<String> getCreatorUnitList() {
		return ListTools.trim(creatorUnitList, true, true);
	}

	public void setApplicationList(List<String> applicationList) {
		this.applicationList = applicationList;
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
}