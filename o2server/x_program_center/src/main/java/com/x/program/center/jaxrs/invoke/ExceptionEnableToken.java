package com.x.program.center.jaxrs.invoke;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEnableToken extends LanguagePromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionEnableToken(String name) {
		super("인터페이스에 토큰 인증이 사용 설정되어 있습니다. 토큰 인증으로 호출하세요: {}.", name);
	}
}
