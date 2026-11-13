package com.x.program.center.jaxrs.collect;

import com.x.base.core.project.exception.PromptException;

class CodeAnswerEmptyException extends PromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	CodeAnswerEmptyException() {
		super("SMS 인증 코드는 비워 둘 수 없습니다.");
	}
}
