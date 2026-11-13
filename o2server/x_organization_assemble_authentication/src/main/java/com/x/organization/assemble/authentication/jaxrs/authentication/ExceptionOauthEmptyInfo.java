package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionOauthEmptyInfo extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	public static String defaultMessage = "요청 정보 응답값이 비어 있습니다.";

	ExceptionOauthEmptyInfo() {
		super(defaultMessage);
	}
}
