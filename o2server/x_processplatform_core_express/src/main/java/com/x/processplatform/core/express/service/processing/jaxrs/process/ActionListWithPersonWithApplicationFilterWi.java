package com.x.processplatform.core.express.service.processing.jaxrs.process;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionListWithPersonWithApplicationFilterWi extends GsonPropertyObject {

	private static final long serialVersionUID = -2305165705076347869L;

	@FieldDescribe("프로세스를 시작할 수 있는 단말 유형, 가능한 값 client,mobile,all.")
	@Schema(description = "프로세스를 시작할 수 있는 단말 유형, 가능한 값 client,mobile,all.")
	private String startableTerminal;

	public String getStartableTerminal() {
		return startableTerminal;
	}

	public void setStartableTerminal(String startableTerminal) {
		this.startableTerminal = startableTerminal;
	}
}