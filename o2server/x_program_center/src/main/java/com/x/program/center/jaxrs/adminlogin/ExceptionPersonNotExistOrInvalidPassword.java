package com.x.program.center.jaxrs.adminlogin;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonNotExistOrInvalidPassword extends PromptException {


	private static final long serialVersionUID = 2537120821114609351L;
	private static final String DEFAULT_MESSAGE = "사용자가 없거나 비밀번호가 올바르지 않습니다.";

	ExceptionPersonNotExistOrInvalidPassword( ) {
		super(DEFAULT_MESSAGE);
	}
}
