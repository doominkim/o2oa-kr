package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionSendCodeError extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public static String defaultMessage = "전송에 실패했습니다.";

	ExceptionSendCodeError() {
		super(defaultMessage);
	}

	ExceptionSendCodeError(Throwable cause) {
		super(cause, defaultMessage);
	}
}
