package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionTwicePasswordNotMatch extends LanguagePromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	ExceptionTwicePasswordNotMatch() {
		super("두 번 입력한 새 비밀번호가 일치하지 않습니다.");
	}

}
