package com.x.processplatform.core.express.service.processing.jaxrs.record;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class ActionWorkProcessingWi extends GsonPropertyObject {

	private static final long serialVersionUID = -6499166223522045961L;

	@FieldDescribe("기록 유형.")
	private String recordType;
	@FieldDescribe("경로.")
	private String routeName;
	@FieldDescribe("처리 의견.")
	private String opinion;
	@FieldDescribe("업무 문서 기록.")
	private String workLog;
	@FieldDescribe("처리자 조직 전용 식별자.")
	private String distinguishedName;
	@FieldDescribe("일련번호.")
	private String series;

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

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getWorkLog() {
		return workLog;
	}

	public void setWorkLog(String workLog) {
		this.workLog = workLog;
	}

	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

}
