package com.x.processplatform.core.express.service.processing.jaxrs.keylock;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionLockWo extends GsonPropertyObject {

	private static final long serialVersionUID = 1864818073446091726L;

	@FieldDescribe("성공 여부.")
	@Schema(description = "성공 여부.")
	private Boolean success;

	@FieldDescribe("잠금 사용자.")
	@Schema(description = "잠금 사용자.")
	private String person;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

}