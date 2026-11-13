package com.x.base.core.project.jaxrs;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public abstract class WrapClearCacheRequest extends GsonPropertyObject {

	public static final String TYPE_NOTIFY = "notify";
	public static final String TYPE_RECEIVE = "receive";

	private static final long serialVersionUID = -4221561865395746387L;

	private String type;

	@FieldDescribe("노드명.")
	@Schema(description = "노드명.")
	private String nodeName;

	@FieldDescribe("클래스명.")
	@Schema(description = "클래스명.")
	private String className;

	@FieldDescribe("키워드.")
	@Schema(description = "키워드.")
	private List<Object> keys;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<Object> getKeys() {
		return null == keys ? new ArrayList<>() : keys;
	}

	public void setKeys(List<Object> keys) {
		this.keys = keys;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}