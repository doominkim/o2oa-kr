package com.x.base.core.project.jaxrs;

import com.x.base.core.project.annotation.FieldDescribe;

public abstract class WoCallback<T> {

	@FieldDescribe("콜백 함수명")
	private String callbackName;

	private T object;

	public WoCallback(String callbackName, T t) {
		this.callbackName = callbackName;
		this.object = t;
	}

	public String getCallbackName() {
		return callbackName;
	}

	public void setCallbackName(String callbackName) {
		this.callbackName = callbackName;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

}
