package com.x.program.center.jaxrs.module;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionValidateError extends LanguagePromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionValidateError() {
		super("클라우드 서버 계정 또는 비밀번호가 올바르지 않습니다.");
	}
}
