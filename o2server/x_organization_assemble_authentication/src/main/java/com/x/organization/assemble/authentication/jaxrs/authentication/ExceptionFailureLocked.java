package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionFailureLocked extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public static String defaultMessage = "계정이 잠겼습니다. 잠금 해제 시간: {} .";

	ExceptionFailureLocked(String time) {
		super(defaultMessage, time);
	}
}
