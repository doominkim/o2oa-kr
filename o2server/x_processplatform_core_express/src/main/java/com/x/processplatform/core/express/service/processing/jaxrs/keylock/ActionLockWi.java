package com.x.processplatform.core.express.service.processing.jaxrs.keylock;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionLockWi extends GsonPropertyObject {

	private static final long serialVersionUID = -7682192241303944945L;

	@FieldDescribe("업무 문서 잠금 식별자.")
	@Schema(description = "업무 문서 잠금 식별자.")
	private String key;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}