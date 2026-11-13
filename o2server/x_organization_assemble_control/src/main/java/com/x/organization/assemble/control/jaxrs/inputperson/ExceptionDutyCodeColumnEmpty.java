package com.x.organization.assemble.control.jaxrs.inputperson;

import com.x.base.core.project.exception.PromptException;

class ExceptionDutyCodeColumnEmpty extends PromptException {

	private static final long serialVersionUID = -2139584911736169462L;

	ExceptionDutyCodeColumnEmpty() {
		super("직무 소속 조직 고유 코드 열은 비워 둘 수 없습니다.");
	}
}
