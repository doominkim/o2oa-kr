package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionConfirmPasswordEmpty extends LanguagePromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	ExceptionConfirmPasswordEmpty() {
		super("비밀번호 확인은 비워 둘 수 없습니다.");
	}

}
