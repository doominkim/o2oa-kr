package com.x.processplatform.core.express.assemble.surface.jaxrs.work;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class ActionListCountWithApplicationWo extends GsonPropertyObject {

	private static final long serialVersionUID = 3629754778852450993L;

	public ActionListCountWithApplicationWo() {
	}

	public ActionListCountWithApplicationWo(String value, String name, Long count) {
		this.value = value;
		this.name = name;
		this.count = count;
	}

	@FieldDescribe("앱 이름")
	private String value;

	@FieldDescribe("앱 식별자")
	private String name;

	@FieldDescribe("수량")
	private Long count;

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
