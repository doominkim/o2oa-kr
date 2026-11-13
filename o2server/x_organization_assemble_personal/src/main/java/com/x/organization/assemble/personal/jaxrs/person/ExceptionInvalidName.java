package com.x.organization.assemble.personal.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

 class ExceptionInvalidName extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	 public static String defaultMessage = "사용자 이름 오류: 비워 둘 수 없고, 예약어나 특수 문자를 사용할 수 없습니다: {}.";

	 ExceptionInvalidName(String name) {
		super(defaultMessage, name);
	}
}
