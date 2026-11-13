package com.x.program.center.jaxrs.code;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDisable extends LanguagePromptException {

	private static final long serialVersionUID = 9107373129400635015L;

	ExceptionDisable() {
		super("클라우드 서버 연결 기능이 사용 설정되어 있지 않습니다.");
	}
}
