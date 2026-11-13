package com.x.organization.assemble.personal.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

 class ExceptionEmployeeDuplicate extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	 public static String defaultMessage = "사용자 이름 오류: {}, {}이(가) 기존 값과 중복됩니다.";

	 ExceptionEmployeeDuplicate(String name, String fieldName) {
		super(defaultMessage, name, fieldName);
	}
}
