package com.x.processplatform.core.entity.element;

import org.apache.commons.lang3.StringUtils;

/**
 * @author sword
 * 流程中配置需要记录数据变化的字段配置方式
 */
public enum DataTraceFieldTypeEnum {

	ALL("all", "모든 필드 기록(대용량 필드와 객체 필드 제외)"),
	CUSTOM("custom", "사용자 정의 필드 기록"),
	NONE("none", "기록 안 함");

	private String value;
	private String name;

	private DataTraceFieldTypeEnum(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public static boolean toTrace(String value) {
		if(StringUtils.isBlank(value)){
			return false;
		}
		if(ALL.value.equals(value) || CUSTOM.value.equals(value)){
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
