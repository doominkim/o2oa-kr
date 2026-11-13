package com.x.processplatform.core.express.service.processing.jaxrs.work;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class V2AddSplitWi extends GsonPropertyObject {

	private static final long serialVersionUID = 5556104841725659921L;

	@FieldDescribe("추가할 분할 값.")
	@Schema(description = "추가할 분할 값.")
	private List<String> splitValueList;

	@FieldDescribe("분기를 추가할 업무 문서 로그 식별자.")
	@Schema(description = "분기를 추가할 업무 문서 로그 식별자.")
	private String workLog;

	public List<String> getSplitValueList() {
		return splitValueList;
	}

	public void setSplitValueList(List<String> splitValueList) {
		this.splitValueList = splitValueList;
	}

	public String getWorkLog() {
		return workLog;
	}

	public void setWorkLog(String workLog) {
		this.workLog = workLog;
	}

}