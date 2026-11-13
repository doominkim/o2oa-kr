package com.x.organization.assemble.personal.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNewPasswordEmpty extends LanguagePromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	public static String defaultMessage = "새 비밀번호는 비워 둘 수 없습니다.";

	ExceptionNewPasswordEmpty() {
		super(defaultMessage);
	}

}
