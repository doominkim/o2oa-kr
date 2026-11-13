package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionOauthBindDisable extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	public static String defaultMessage = "연동이 비활성화되었습니다.";

	ExceptionOauthBindDisable() {
		super(defaultMessage);
	}
}
