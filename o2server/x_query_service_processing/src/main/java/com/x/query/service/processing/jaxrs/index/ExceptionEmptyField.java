package com.x.query.service.processing.jaxrs.index;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyField extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionEmptyField(String name) {
		super("필드는 비워 둘 수 없습니다: {}.", name);
	}

}
