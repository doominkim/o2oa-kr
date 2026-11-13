package com.x.organization.assemble.express.jaxrs.empowerlog;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyApplication extends PromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionEmptyApplication() {
		super("앱은 비워 둘 수 없습니다.");
	}
}
