package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonNotExistOrInvalidAnswer extends PromptException {

	private static final long serialVersionUID = -8334021007462970656L;
	public static String defaultMessage = "사용자가 없거나 인증 코드가 올바르지 않습니다.";

	ExceptionPersonNotExistOrInvalidAnswer( ) {
		super(defaultMessage);
	}
}
