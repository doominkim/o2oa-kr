package com.x.processplatform.core.express.service.processing.jaxrs.task;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class ActionProcessingWi extends GsonPropertyObject {

	private static final long serialVersionUID = -2272062779655632688L;

	public static final String PROCESSINGTYPE_TASK = "task";

	public static final String PROCESSINGTYPE_RESET = "reset";

	@FieldDescribe("흐름 유형.")
	private String processingType;

	@FieldDescribe("경로 결정.")
	private String routeName;

	@FieldDescribe("결재 대기 의견.")
	private String opinion;

	public String getProcessingType() {
		return processingType;
	}

	public void setProcessingType(String processingType) {
		this.processingType = processingType;
	}

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

}
