package com.x.processplatform.core.entity.content;

public enum DocSignStatus {

	STATUS_1(1, "임시저장"),
	STATUS_2(2, "서명 본문 수정 불가"),
	STATUS_3(3, "서명 본문 수정 가능");
	private Integer value;
	private String name;

	private DocSignStatus(Integer value, String name) {
		this.value = value;
		this.name = name;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

}
