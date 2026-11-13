package com.x.processplatform.assemble.designer.jaxrs.templateform;

import com.x.base.core.project.exception.PromptException;

class ExceptionInsufficientPermission extends PromptException {

	private static final long serialVersionUID = 1148555249431355284L;

	ExceptionInsufficientPermission(String person) {
		super("사용자: {}에게 이 작업을 수행할 권한이 없습니다.", person);
	}
}
