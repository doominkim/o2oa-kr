package com.x.organization.assemble.authentication.jaxrs.sso;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionReadToken extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	public static String defaultMessage = "유효하지 않은 token, client: {}, token: {}.";

	ExceptionReadToken(String client, String token) {
		super(defaultMessage, client, token);
	}
}
