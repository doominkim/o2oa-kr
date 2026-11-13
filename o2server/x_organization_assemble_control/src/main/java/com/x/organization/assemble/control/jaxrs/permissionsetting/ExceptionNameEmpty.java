package com.x.organization.assemble.control.jaxrs.permissionsetting;

import com.x.base.core.project.exception.PromptException;

class ExceptionNameEmpty extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionNameEmpty() {
		super("이름은 비워 둘 수 없습니다.");
	}
}
