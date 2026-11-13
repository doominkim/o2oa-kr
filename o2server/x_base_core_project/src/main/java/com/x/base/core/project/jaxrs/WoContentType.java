package com.x.base.core.project.jaxrs;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class WoContentType extends GsonPropertyObject {

	public WoContentType() {
	}

	public WoContentType(Object body, String contentType) {
		this.body = body;
		this.contentType = contentType;
	}

	@FieldDescribe("주소.")
	private String contentType;

	@FieldDescribe("내용.")
	private Object body;

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

}
