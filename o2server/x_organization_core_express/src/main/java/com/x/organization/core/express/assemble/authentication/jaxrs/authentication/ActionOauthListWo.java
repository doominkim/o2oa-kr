package com.x.organization.core.express.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionOauthListWo extends GsonPropertyObject {

	private static final long serialVersionUID = -4816908647083844614L;

	@FieldDescribe("이름.")
	@Schema(description = "이름.")
	private String name;

	@FieldDescribe("표시 이름.")
	@Schema(description = "표시 이름..")
	private String displayName;

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

	@FieldDescribe("계정 연동 사용 여부.")
	@Schema(description = "계정 연동 사용 여부.")
	private Boolean bindingEnable;

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

	public Boolean getBindingEnable() {
		return bindingEnable;
	}

	public void setBindingEnable(Boolean bindingEnable) {
		this.bindingEnable = bindingEnable;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}