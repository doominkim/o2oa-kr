package com.x.query.core.express.index;

import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

public class Directory {

	@FieldDescribe("분류.")
	@Schema(description = "분류.")
	private String category;

	@FieldDescribe("이름.")
	@Schema(description = "이름.")
	private String name;

	@FieldDescribe("식별자.")
	@Schema(description = "식별자.")
	private String key;

	public Directory() {
	}

	public Directory(String category, String name, String key) {
		this.category = category;
		this.name = name;
		this.key = key;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}