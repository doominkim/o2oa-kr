package com.x.processplatform.core.express.assemble.surface.jaxrs.anonymous;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionReadCountWithPersonWo extends GsonPropertyObject {

	private static final long serialVersionUID = 8792811593252273112L;

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