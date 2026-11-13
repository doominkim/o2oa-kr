package com.x.message.assemble.communicate.jaxrs.mass;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyTarget extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionEmptyTarget() {
		super("받는 대상은 비워 둘 수 없습니다.");
	}
}
