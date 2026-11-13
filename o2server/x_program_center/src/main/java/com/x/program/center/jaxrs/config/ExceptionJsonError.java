package com.x.program.center.jaxrs.config;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionJsonError extends LanguagePromptException {

	private static final long serialVersionUID = 6084637626229970254L;

	ExceptionJsonError() {
		super("내용이 올바른 json 형식이 아닙니다.");
	}
}
