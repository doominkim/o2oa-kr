package com.x.processplatform.core.express.assemble.surface.jaxrs.readcompleted;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionCountWithPersonWo extends GsonPropertyObject {

	private static final long serialVersionUID = 2387906569643215824L;

	@FieldDescribe("열람 완료 수량.")
	@Schema(description = "열람 완료 수량.")
	private Long count = 0L;

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

}