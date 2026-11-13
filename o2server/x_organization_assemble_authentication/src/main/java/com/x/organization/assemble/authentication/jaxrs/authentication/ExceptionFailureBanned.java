package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionFailureBanned extends PromptException {

	private static final long serialVersionUID = -2174848875972265963L;

	public static String defaultMessage = "계정 접근이 차단되었습니다. 관리자에게 문의하세요.";

	ExceptionFailureBanned() {
		super(defaultMessage);
	}
}
