package com.x.organization.assemble.authentication.jaxrs.sso;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyCredential extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;
	public static String defaultMessage = "이름이 비어 있습니다.";

	ExceptionEmptyCredential() {
		super(defaultMessage);
	}
}
