package com.x.program.center.core.entity;

/**
 *
 * @author sword
 */
public enum InstallTypeEnum {

	DATA("data", "초기화 데이터"),
	CUSTOM("custom", "사용자 정의 서비스"),
	XAPP("xapp", "플랫폼 설치 패키지"),
	CONFIG("config", "설정 파일"),
	WEB("web", "프런트엔드 리소스");

	private String value;
	private String name;

	private InstallTypeEnum(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public static InstallTypeEnum getByValue(String value) {
		for (InstallTypeEnum e : InstallTypeEnum.values()) {
			if (e.getValue().equals(value)) {
				return e;
			}
		}
		return null;
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
