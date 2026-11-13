package com.x.processplatform.core.entity.content;


/**
 * @author sword
 */
public enum HandoverStatusEnum {

	WAIT("wait", "실행 대기"),
	CANCEL("cancel", "취소됨"),
	PROCESSING("processing", "실행 중"),
	PROCESSED("processed", "실행 완료");

	private String value;
	private String name;

	private HandoverStatusEnum(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public static String getNameByValue(String value) {
		for (HandoverStatusEnum e : HandoverStatusEnum.values()) {
			if (e.getValue().equals(value)) {
				return e.name;
			}
		}
		return value;
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
