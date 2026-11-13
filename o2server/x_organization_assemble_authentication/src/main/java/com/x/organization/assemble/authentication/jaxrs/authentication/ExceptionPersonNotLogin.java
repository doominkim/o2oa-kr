package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPersonNotLogin extends LanguagePromptException {

	private static final long serialVersionUID = -6124481323896411121L;

	public static String defaultMessage = "로그인하지 않았습니다.";

	ExceptionPersonNotLogin() {
		super(defaultMessage);
	}
}
