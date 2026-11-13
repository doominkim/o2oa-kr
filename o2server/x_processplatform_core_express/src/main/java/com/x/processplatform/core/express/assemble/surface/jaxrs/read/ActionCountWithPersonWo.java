package com.x.processplatform.core.express.assemble.surface.jaxrs.read;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionCountWithPersonWo extends GsonPropertyObject {

	private static final long serialVersionUID = -4242925327577316593L;
	
	@FieldDescribe("열람 대기 수량.")
	@Schema(description = "열람 대기 수량.")
	private Long count = 0L;

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

}