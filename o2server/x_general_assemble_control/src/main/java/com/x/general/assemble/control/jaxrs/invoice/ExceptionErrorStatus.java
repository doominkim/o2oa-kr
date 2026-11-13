package com.x.general.assemble.control.jaxrs.invoice;

import com.x.base.core.project.exception.PromptException;

class ExceptionErrorStatus extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionErrorStatus() {
		super("이미 정산된 영수증이라 삭제할 수 없습니다.");
	}
}
