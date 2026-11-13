package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionOauthEmptyCredential extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	public static String defaultMessage = "사용자 자격 증명을 가져올 수 없습니다.";

	ExceptionOauthEmptyCredential() {
		super(defaultMessage);
	}
}
