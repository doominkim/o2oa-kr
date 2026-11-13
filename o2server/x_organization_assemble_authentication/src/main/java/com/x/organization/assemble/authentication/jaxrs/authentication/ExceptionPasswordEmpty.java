package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPasswordEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public static String defaultMessage = "비밀번호가 비어 있거나 비밀번호 복호화에 실패했습니다.";

	ExceptionPasswordEmpty() {
		super(defaultMessage);
	}
}
