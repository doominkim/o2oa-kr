package com.x.processplatform.core.express.assemble.surface.jaxrs.task;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionManageProcessingWi extends GsonPropertyObject {

	private static final long serialVersionUID = -3684461752407390735L;

	@FieldDescribe("처리 선택 경로 이름.")
	@Schema(description = "처리 선택 경로 이름.")
	private String routeName;
	@FieldDescribe("처리 의견.")
	@Schema(description = "처리 의견.")
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
	
	

}
