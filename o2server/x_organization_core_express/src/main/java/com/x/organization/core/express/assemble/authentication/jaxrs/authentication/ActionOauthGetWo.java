package com.x.organization.core.express.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionOauthGetWo extends GsonPropertyObject {

	private static final long serialVersionUID = 4777997438663875200L;

	@FieldDescribe("이름.")
	@Schema(description = "이름.")
	private String name;

	@FieldDescribe("리디렉션 주소.")
	@Schema(description = "리디렉션 주소.")
	private String redirectUri;

	@FieldDescribe("인증 주소.")
	@Schema(description = "인증 주소.")
	private String authAddress;

	@FieldDescribe("인증 방식 유형.")
	@Schema(description = "인증 방식 유형.")
	private String authMethod;

	@FieldDescribe("인증 매개변수.")
	@Schema(description = "인증 매개변수.")
	private String authParameter;

	@FieldDescribe("아이콘.")
	@Schema(description = "아이콘.")
	private String icon;

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public String getAuthMethod() {
		return authMethod;
	}

	public void setAuthMethod(String authMethod) {
		this.authMethod = authMethod;
	}

	public String getAuthParameter() {
		return authParameter;
	}

	public void setAuthParameter(String authParameter) {
		this.authParameter = authParameter;
	}

	public String getAuthAddress() {
		return authAddress;
	}

	public void setAuthAddress(String authAddress) {
		this.authAddress = authAddress;
	}
}