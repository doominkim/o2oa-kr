package com.x.program.center.jaxrs.invoke;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidRemoteAddr extends LanguagePromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionInvalidRemoteAddr(String addr, String name) {
		super("유효하지 않은 접근 주소: {}, 인터페이스 이름: {}.", addr, name);
	}
}
