package com.x.organization.assemble.control.jaxrs.inputperson;

import com.x.base.core.project.exception.PromptException;

class ExceptionResultNotFound extends PromptException {

	private static final long serialVersionUID = -2139584911736169462L;

	ExceptionResultNotFound(String flag) {
		super("가져오기 결과를 찾을 수 없습니다: {}.", flag);
	}
}
