package com.x.processplatform.core.express.service.processing.jaxrs.read;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionManageResetWi extends GsonPropertyObject {

	private static final long serialVersionUID = -3686768943199603543L;

	@FieldDescribe("열람 대기자 신분 재설정.")
	@Schema(description = "열람 대기자 신분 재설정.")
	private List<String> identityList;

	@FieldDescribe("열람 대기 의견.")
	@Schema(description = "열람 대기 의견.")
	private String opinion;

	public List<String> getIdentityList() {
		return identityList;
	}

	public void setIdentityList(List<String> identityList) {
		this.identityList = identityList;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

}