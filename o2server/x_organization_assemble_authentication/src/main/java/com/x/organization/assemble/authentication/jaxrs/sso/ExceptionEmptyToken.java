package com.x.organization.assemble.authentication.jaxrs.sso;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyToken extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;
	public static String defaultMessage = "sso에서 디코딩할 토큰을 제공하지 않았습니다.";

	ExceptionEmptyToken() {
		super(defaultMessage);
	}
}
