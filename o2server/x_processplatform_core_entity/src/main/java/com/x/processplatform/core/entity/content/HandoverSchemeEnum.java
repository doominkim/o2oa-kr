package com.x.processplatform.core.entity.content;


/**
 * @author sword
 */
public enum HandoverSchemeEnum {

	ALL("all", "권한이 있는 모든 문서"),
	APPLICATION("application", "지정한 앱에서 권한이 있는 문서"),
	PROCESS("process", "지정한 결재에서 권한이 있는 문서"),
	JOB("job", "지정한 권한 있는 문서");

	private String value;
	private String name;

	private HandoverSchemeEnum(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public static String getNameByValue(String value) {
		for (HandoverSchemeEnum e : HandoverSchemeEnum.values()) {
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
