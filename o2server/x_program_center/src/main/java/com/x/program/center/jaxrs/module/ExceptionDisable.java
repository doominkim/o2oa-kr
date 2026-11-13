package com.x.program.center.jaxrs.module;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDisable extends LanguagePromptException {

	private static final long serialVersionUID = 9107373129400635015L;

	ExceptionDisable() {
		super("등록 서버 연결 기능이 사용 설정되어 있지 않습니다.");
	}
}
