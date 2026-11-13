package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPersonNotExistOrInvalidPassword extends LanguagePromptException {

	private static final long serialVersionUID = -6124481323896411121L;

	public static String defaultMessage = "사용자가 없거나 비밀번호가 올바르지 않습니다.";

	ExceptionPersonNotExistOrInvalidPassword( ) {
		super(defaultMessage);
	}
}
