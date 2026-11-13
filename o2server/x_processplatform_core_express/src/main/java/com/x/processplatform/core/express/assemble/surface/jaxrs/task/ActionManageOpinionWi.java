package com.x.processplatform.core.express.assemble.surface.jaxrs.task;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionManageOpinionWi extends GsonPropertyObject {

	private static final long serialVersionUID = -962598264584773600L;
	@FieldDescribe("처리 의견.")
	@Schema(description = "처리 의견.")
	private String opinion;

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

}