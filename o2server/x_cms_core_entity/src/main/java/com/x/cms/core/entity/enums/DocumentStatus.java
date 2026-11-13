package com.x.cms.core.entity.enums;

/**
 * 内容管理文档状态
 * @author sword
 */
public enum DocumentStatus {

	DRAFT("draft", "임시저장"),
	PUBLISHED("published", "게시됨"),
	WAIT_PUBLISH("waitPublish", "게시 대기"),
	ARCHIVED("archived", "보관");
	private String value;
	private String name;

	private DocumentStatus(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public static boolean isEndStatus(String status){
		if(PUBLISHED.getValue().equals(status) || ARCHIVED.getValue().equals(status)){
			return true;
		}
		return false;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value == null ? null : value.trim();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

}
