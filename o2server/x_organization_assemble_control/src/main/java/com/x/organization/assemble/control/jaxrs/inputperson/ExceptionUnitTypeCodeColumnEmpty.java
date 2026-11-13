package com.x.organization.assemble.control.jaxrs.inputperson;

import com.x.base.core.project.exception.PromptException;

class ExceptionUnitTypeCodeColumnEmpty extends PromptException {

	private static final long serialVersionUID = -2139584911736169462L;

	ExceptionUnitTypeCodeColumnEmpty() {
		super("상위 조직 번호는 비워 둘 수 없습니다.");
	}
}
