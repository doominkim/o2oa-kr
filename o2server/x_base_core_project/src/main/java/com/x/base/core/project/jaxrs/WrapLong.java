package com.x.base.core.project.jaxrs;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapLong extends GsonPropertyObject {

	public WrapLong() {

	}

	public WrapLong(Long o) {
		this.value = o;
	}

	@FieldDescribe("장정수 값")
	private Long value;

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

}
