package com.x.processplatform.core.express.service.processing.jaxrs.read;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionCreateWithWorkCompletedWi extends GsonPropertyObject {

	private static final long serialVersionUID = 7061824634357637666L;

	@FieldDescribe("신분 식별자.")
	private List<String> identityList = new ArrayList<>();

	@FieldDescribe("열람 대기 알림 발송.")
	private Boolean notify = false;

	@FieldDescribe("발신자")
	private String sender = "";

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public List<String> getIdentityList() {
		return identityList;
	}

	public void setIdentityList(List<String> identityList) {
		this.identityList = identityList;
	}

	public Boolean getNotify() {
		return notify;
	}

	public void setNotify(Boolean notify) {
		this.notify = notify;
	}

}