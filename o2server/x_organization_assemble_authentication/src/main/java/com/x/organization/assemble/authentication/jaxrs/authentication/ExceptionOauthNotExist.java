package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionOauthNotExist extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	public static String defaultMessage = "이름이 {} 인 OauthClient 로그인 설정을 찾을 수 없습니다.";

	ExceptionOauthNotExist(String name) {
		super(defaultMessage, name);
	}
}
