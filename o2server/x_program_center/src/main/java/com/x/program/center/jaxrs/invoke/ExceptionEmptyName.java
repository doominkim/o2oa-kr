package com.x.program.center.jaxrs.invoke;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyName extends LanguagePromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionEmptyName() {
		super("인터페이스 이름은 비워 둘 수 없습니다.");
	}
}
