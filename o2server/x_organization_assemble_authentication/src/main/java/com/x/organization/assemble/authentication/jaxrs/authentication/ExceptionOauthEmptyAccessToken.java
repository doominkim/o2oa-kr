package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionOauthEmptyAccessToken extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	public static String defaultMessage = "액세스 토큰을 가져올 수 없습니다.";

	ExceptionOauthEmptyAccessToken() {
		super(defaultMessage);
	}
}
