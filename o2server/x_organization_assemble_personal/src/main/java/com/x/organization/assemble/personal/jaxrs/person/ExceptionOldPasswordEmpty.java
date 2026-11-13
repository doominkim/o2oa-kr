package com.x.organization.assemble.personal.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionOldPasswordEmpty extends LanguagePromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	ExceptionOldPasswordEmpty() {
		super("기존 비밀번호는 비워 둘 수 없습니다.");
	}

}
