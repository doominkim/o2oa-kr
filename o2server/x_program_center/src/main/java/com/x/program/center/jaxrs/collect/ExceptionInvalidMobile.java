package com.x.program.center.jaxrs.collect;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidMobile extends LanguagePromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionInvalidMobile(String mobile) {
		super("휴대폰 번호 {} 오류: 비워 둘 수 없으며 휴대폰 번호 형식이어야 합니다.", mobile);
	}
}
