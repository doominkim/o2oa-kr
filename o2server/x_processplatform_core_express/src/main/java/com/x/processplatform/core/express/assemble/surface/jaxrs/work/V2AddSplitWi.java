package com.x.processplatform.core.express.assemble.surface.jaxrs.work;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class V2AddSplitWi extends GsonPropertyObject {

	private static final long serialVersionUID = 5556104841725659921L;

	@FieldDescribe("추가할 분할 값.")
	@Schema(description = "추가할 분할 값.")
	private List<String> splitValueList;

	@FieldDescribe("이미 존재하는 분할 값 제외 여부.")
	@Schema(description = "이미 존재하는 분할 값 제외 여부.")
	private Boolean trimExist;

	@FieldDescribe("분기를 추가할 업무 문서 로그 식별자.")
	@Schema(description = "분기를 추가할 업무 문서 로그 식별자.")
	private String workLog;

	@FieldDescribe("경로.")
	private String routeName;
	@FieldDescribe("처리 의견.")
	private String opinion;

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public List<String> getSplitValueList() {
		return splitValueList;
	}

	public void setSplitValueList(List<String> splitValueList) {
		this.splitValueList = splitValueList;
	}

	public Boolean getTrimExist() {
		return trimExist;
	}

	public void setTrimExist(Boolean trimExist) {
		this.trimExist = trimExist;
	}

	public String getWorkLog() {
		return workLog;
	}

	public void setWorkLog(String workLog) {
		this.workLog = workLog;
	}

}