package com.x.organization.core.express.assemble.authentication.jaxrs.sso;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class ActionPostEncryptWi extends GsonPropertyObject {

	private static final long serialVersionUID = -8450939016187545724L;
	@FieldDescribe("클라이언트 식별자")
	private String client;
	@FieldDescribe("사용자 식별자")
	private String credential;
	@FieldDescribe("암호화 키")
	private String key;

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
