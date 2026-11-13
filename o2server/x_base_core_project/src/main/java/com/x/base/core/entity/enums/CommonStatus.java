package com.x.base.core.entity.enums;

/**
 * 공통 상태 열거형 클래스
 */
public enum CommonStatus {

	VALID("1", "정상"), INVALID("0", "삭제됨");
	private String value;
	private String name;

	private CommonStatus(String value, String name) {
		this.value = value;
		this.name = name;
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
