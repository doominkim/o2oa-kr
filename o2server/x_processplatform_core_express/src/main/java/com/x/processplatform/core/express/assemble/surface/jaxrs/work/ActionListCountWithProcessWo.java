package com.x.processplatform.core.express.assemble.surface.jaxrs.work;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionListCountWithProcessWo extends GsonPropertyObject {

	private static final long serialVersionUID = 3947731081213211239L;

	@FieldDescribe("프로세스 식별자.")
	@Schema(description = "프로세스 식별자.")
	private String value;

	@FieldDescribe("프로세스 이름.")
	@Schema(description = "프로세스 이름.")
	private String name;

	@FieldDescribe("수량.")
	@Schema(description = "수량.")
	private Long count;

	public ActionListCountWithProcessWo() {

	}

	public ActionListCountWithProcessWo(String value, String name, Long count) {
		this.value = value;
		this.name = name;
		this.count = count;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

}