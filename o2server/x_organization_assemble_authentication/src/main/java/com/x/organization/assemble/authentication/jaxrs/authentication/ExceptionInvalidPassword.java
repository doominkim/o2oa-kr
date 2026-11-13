package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidPassword extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public static String defaultMessage = "사용자 이름 또는 비밀번호가 올바르지 않습니다.";

	ExceptionInvalidPassword() {
		super(defaultMessage);
	}
}
