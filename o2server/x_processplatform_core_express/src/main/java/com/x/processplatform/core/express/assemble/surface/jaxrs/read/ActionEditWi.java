package com.x.processplatform.core.express.assemble.surface.jaxrs.read;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionEditWi extends GsonPropertyObject {

	private static final long serialVersionUID = 6597364646543894761L;
	
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