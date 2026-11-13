package com.x.processplatform.core.express.assemble.surface.jaxrs.read;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionCountWithFilterWi extends GsonPropertyObject {

	private static final long serialVersionUID = -5626519123284286315L;

	@FieldDescribe("사용자 식별자.")
	@Schema(description = "사용자 식별자.")
	private List<String> credentialList;

	@FieldDescribe("앱 식별자.")
	@Schema(description = "앱 식별자.")
	private List<String> appliationList;

	@FieldDescribe("프로세스 식별자.")
	@Schema(description = "프로세스 식별자.")
	private List<String> processList;

	public List<String> getCredentialList() {
		return credentialList;
	}

	public void setCredentialList(List<String> credentialList) {
		this.credentialList = credentialList;
	}

	public List<String> getAppliationList() {
		return appliationList;
	}

	public void setAppliationList(List<String> appliationList) {
		this.appliationList = appliationList;
	}

	public List<String> getProcessList() {
		return processList;
	}

	public void setProcessList(List<String> processList) {
		this.processList = processList;
	}

}