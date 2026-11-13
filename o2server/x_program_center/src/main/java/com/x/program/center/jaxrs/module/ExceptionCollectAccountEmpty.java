package com.x.program.center.jaxrs.module;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionCollectAccountEmpty extends LanguagePromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionCollectAccountEmpty() {
		super("설정의 클라우드 서버 로그인 계정 또는 비밀번호가 비어 있습니다.");
	}
}
