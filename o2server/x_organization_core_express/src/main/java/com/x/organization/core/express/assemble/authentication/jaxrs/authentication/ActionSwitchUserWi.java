package com.x.organization.core.express.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionSwitchUserWi extends GsonPropertyObject {

	private static final long serialVersionUID = 8266452052001813770L;
	
	@FieldDescribe("사용자 자격 증명.")
	@Schema(description = "사용자 자격 증명.")
	private String credential;

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

}