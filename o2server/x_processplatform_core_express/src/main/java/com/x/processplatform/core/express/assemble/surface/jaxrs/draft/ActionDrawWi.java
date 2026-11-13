package com.x.processplatform.core.express.assemble.surface.jaxrs.draft;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.cms.core.entity.content.Data;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionDrawWi extends GsonPropertyObject {

	private static final long serialVersionUID = -7496462800015649736L;

	@FieldDescribe("제목.")
	@Schema(description = "제목.")
	private String title;

	@FieldDescribe("시작 사용자 신분.")
	@Schema(description = "시작 사용자 신분.")
	private String identity;

	@FieldDescribe("업무 데이터.")
	@Schema(description = "업무 데이터.")
	private Data data;

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

}