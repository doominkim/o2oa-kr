package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionLoginDisable extends PromptException {

	private static final long serialVersionUID = 6312558456172881129L;

	public static final String DEFAULT_MESSAGE = "현재 인증 방식은 사용할 수 없습니다.";

	ExceptionLoginDisable() {
		super(DEFAULT_MESSAGE);
	}
}
