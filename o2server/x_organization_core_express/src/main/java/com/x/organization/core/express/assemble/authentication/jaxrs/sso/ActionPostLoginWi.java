package com.x.organization.core.express.assemble.authentication.jaxrs.sso;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class ActionPostLoginWi extends GsonPropertyObject {

	private static final long serialVersionUID = -3195426330909623942L;

	@FieldDescribe("토큰")
	private String token;
	@FieldDescribe("클라이언트 식별자")
	private String client;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

}
