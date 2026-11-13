package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionSendCodeResult extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public static String defaultMessage = "인증 코드를 보냈습니다. 받지 못했다면 해당 번호가 연결되어 있는지 확인하세요.";

	ExceptionSendCodeResult() {
		super(defaultMessage);
	}

	ExceptionSendCodeResult(Throwable cause) {
		super(cause, defaultMessage);
	}
}
