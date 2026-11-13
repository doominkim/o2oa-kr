package com.x.program.center.jaxrs.invoke;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDuplicateAlias extends LanguagePromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionDuplicateAlias(String name) {
		super("인터페이스 별칭: {} 중복.", name);
	}
}
