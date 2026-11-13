package com.x.organization.assemble.personal.jaxrs.password;

import com.x.base.core.project.exception.LanguagePromptException;

class ConfirmPasswordEmptyException extends LanguagePromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	ConfirmPasswordEmptyException() {
		super("비밀번호 확인은 비워 둘 수 없습니다.");
	}

}
