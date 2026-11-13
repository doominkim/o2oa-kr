package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionCredentialEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public static String defaultMessage = "사용자 이름은 비워 둘 수 없습니다.";

	ExceptionCredentialEmpty() {
		super(defaultMessage);
	}
}
