package com.x.organization.assemble.express.jaxrs.empowerlog;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyFromIdentity extends PromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionEmptyFromIdentity() {
		super("위임자는 비워 둘 수 없습니다.");
	}
}
