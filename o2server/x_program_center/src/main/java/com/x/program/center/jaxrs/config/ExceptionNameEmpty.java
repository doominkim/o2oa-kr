package com.x.program.center.jaxrs.config;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNameEmpty extends LanguagePromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionNameEmpty() {
		super("이름은 비워 둘 수 없습니다.");
	}
}
