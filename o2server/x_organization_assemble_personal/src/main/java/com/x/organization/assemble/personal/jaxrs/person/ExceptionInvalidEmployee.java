package com.x.organization.assemble.personal.jaxrs.person;


import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidEmployee extends LanguagePromptException {

	private static final long serialVersionUID = 4622760821556680073L;

	public static String defaultMessage = "사번은 비워 둘 수 없고 특수 문자를 사용할 수 없습니다: {}.";

	ExceptionInvalidEmployee(String unique) {
		super(defaultMessage, unique);
	}
}
