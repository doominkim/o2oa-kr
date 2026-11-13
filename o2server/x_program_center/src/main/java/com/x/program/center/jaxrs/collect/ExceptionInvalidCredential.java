package com.x.program.center.jaxrs.collect;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidCredential extends LanguagePromptException {

	private static final long serialVersionUID = 5173172412837627670L;

	ExceptionInvalidCredential() {
		super("사용자 이름 또는 비밀번호가 올바르지 않아 등록 서버와 통신할 수 없습니다.");
	}
}
