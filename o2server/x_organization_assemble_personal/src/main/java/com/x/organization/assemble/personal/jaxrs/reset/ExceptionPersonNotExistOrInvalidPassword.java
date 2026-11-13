package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonNotExistOrInvalidPassword extends PromptException {


	private static final long serialVersionUID = 2537120821114609351L;
	public static String defaultMessage = "사용자가 없거나 비밀번호가 올바르지 않습니다.";

	ExceptionPersonNotExistOrInvalidPassword( ) {
		super(defaultMessage);
	}
}
