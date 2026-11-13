package com.x.organization.assemble.express.jaxrs.empowerlog;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyProcess extends PromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionEmptyProcess() {
		super("결재는 비워 둘 수 없습니다.");
	}
}
