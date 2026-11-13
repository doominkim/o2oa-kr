package com.x.organization.assemble.authentication.jaxrs.sso;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyKey extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;
	public static String defaultMessage = "sso 설정 token은 비워 둘 수 없습니다.";

	ExceptionEmptyKey() {
		super(defaultMessage);
	}
}
