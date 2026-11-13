package com.x.organization.assemble.control.jaxrs.inputperson;

import com.x.base.core.project.exception.PromptException;

class ExceptionMobileColumnEmpty extends PromptException {

	private static final long serialVersionUID = -2139584911736169462L;

	ExceptionMobileColumnEmpty() {
		super("휴대폰 번호 열은 비워 둘 수 없습니다.");
	}
}
