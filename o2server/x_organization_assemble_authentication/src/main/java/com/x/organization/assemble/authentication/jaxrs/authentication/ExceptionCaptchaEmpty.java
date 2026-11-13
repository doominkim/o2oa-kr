package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionCaptchaEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public static String defaultMessage = "이미지 인증 코드는 비워 둘 수 없습니다.";

	ExceptionCaptchaEmpty() {
		super(defaultMessage);
	}
}
