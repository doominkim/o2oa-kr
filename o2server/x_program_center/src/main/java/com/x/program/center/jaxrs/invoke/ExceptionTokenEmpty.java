package com.x.program.center.jaxrs.invoke;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionTokenEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionTokenEmpty() {
		super("sso에서 디코딩할 토큰을 제공하지 않았습니다.");
	}
}
