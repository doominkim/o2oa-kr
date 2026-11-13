package com.x.program.center.core.entity.wrap;


public enum ServiceModuleEnum {

	AGENT("service-agent","에이전트 서비스"),
	INVOKE("service-invoke","인터페이스 서비스"),
	SCRIPT("service-script","스크립트"),
	DICT("service-dict","데이터 사전");

	private String value;
	private String description;

	ServiceModuleEnum(String value, String description){
		this.value = value;
		this.description = description;
	}

	public String getValue(){
		return this.value;
	}

	public String getDescription(){
		return this.description;
	}

	public static String getDescriptionByValue(String value){
		if(value==null || value.equals("")){
			return "";
		}
		ServiceModuleEnum[] states = ServiceModuleEnum.values();
		for (int i = 0; i < states.length; i++) {
			if(value.equals(states[i].getValue())){
				return states[i].getDescription();
			}
		}
		return "";
	}

	public static ServiceModuleEnum getEnumByValue(String value) {
		ServiceModuleEnum[] werviceModuleEnum = ServiceModuleEnum.values();
		for (int i = 0, len = werviceModuleEnum.length; i < len; ++i) {
			if (werviceModuleEnum[i].value.equals(value)) {
				return werviceModuleEnum[i];
			}
		}
		return null;
	}
}
