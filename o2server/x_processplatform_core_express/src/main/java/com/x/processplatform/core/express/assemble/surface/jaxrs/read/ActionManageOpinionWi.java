package com.x.processplatform.core.express.assemble.surface.jaxrs.read;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionManageOpinionWi extends GsonPropertyObject {

	private static final long serialVersionUID = -6949145106671370447L;

	@FieldDescribe("열람 대기 의견.")
	@Schema(description = "열람 대기 의견.")
	private String opinion;

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

}