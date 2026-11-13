package com.x.organization.core.express.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionCodeLoginWi extends GsonPropertyObject {

	private static final long serialVersionUID = -808566215993013029L;

	@FieldDescribe("사용자 식별자.")
	@Schema(description = "사용자 식별자.")
	private String credential;

	@FieldDescribe("SMS 인증 코드.")
	@Schema(description = "SMS 인증 코드.")
	private String codeAnswer;

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public String getCodeAnswer() {
		return codeAnswer;
	}

	public void setCodeAnswer(String codeAnswer) {
		this.codeAnswer = codeAnswer;
	}

}
