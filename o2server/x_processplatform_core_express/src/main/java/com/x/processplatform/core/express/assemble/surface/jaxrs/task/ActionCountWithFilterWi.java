package com.x.processplatform.core.express.assemble.surface.jaxrs.task;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionCountWithFilterWi extends GsonPropertyObject {

	private static final long serialVersionUID = 5540904413841978134L;

	@FieldDescribe("사용자")
	@Schema(description = "필터 제한 사용자 목록")
	private List<String> credentialList;

	@FieldDescribe("앱")
	@Schema(description = "필터 제한 앱 목록")
	private List<String> applicationList;

	@FieldDescribe("프로세스")
	@Schema(description = "필터 제한 프로세스 목록")
	private List<String> processList;

	public List<String> getCredentialList() {
		return ListTools.trim(credentialList, true, true);
	}

	public List<String> getApplicationList() {
		return ListTools.trim(applicationList, true, true);
	}

	public List<String> getProcessList() {
		return ListTools.trim(processList, true, true);
	}

	public void setCredentialList(List<String> credentialList) {
		this.credentialList = credentialList;
	}

	public void setAppliationList(List<String> applicationList) {
		this.applicationList = applicationList;
	}

	public void setProcessList(List<String> processList) {
		this.processList = processList;
	}

}