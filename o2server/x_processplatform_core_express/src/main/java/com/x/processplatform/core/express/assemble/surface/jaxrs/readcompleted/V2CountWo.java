package com.x.processplatform.core.express.assemble.surface.jaxrs.readcompleted;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.NameValueCountPair;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class V2CountWo extends GsonPropertyObject {

	private static final long serialVersionUID = -8723288394987103327L;

	@FieldDescribe("총 수량.")
	@Schema(description = "총 수량.")
	private Long count;

	@FieldDescribe("앱별 분류 수량.")
	@Schema(description = "앱별 분류 수량.")
	private List<NameValueCountPair> applicationList = new ArrayList<>();

	@FieldDescribe("프로세스별 분류 수량.")
	@Schema(description = "프로세스별 분류 수량.")
	private List<NameValueCountPair> processList = new ArrayList<>();

	@FieldDescribe("생성자별 분류 수량.")
	@Schema(description = "생성자별 분류 수량.")
	private List<NameValueCountPair> creatorPersonList = new ArrayList<>();

	@FieldDescribe("생성 조직별 분류 수량.")
	@Schema(description = "생성 조직별 분류 수량.")
	private List<NameValueCountPair> creatorUnitList = new ArrayList<>();

	@FieldDescribe("생성 연월별 분류.")
	@Schema(description = "생성 연월별 분류.")
	private List<NameValueCountPair> startTimeMonthList = new ArrayList<>();

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

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

	public List<NameValueCountPair> getCreatorPersonList() {
		return creatorPersonList;
	}

	public void setCreatorPersonList(List<NameValueCountPair> creatorPersonList) {
		this.creatorPersonList = creatorPersonList;
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

}