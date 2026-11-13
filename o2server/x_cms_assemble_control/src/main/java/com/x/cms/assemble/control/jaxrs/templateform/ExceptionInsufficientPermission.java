package com.x.cms.assemble.control.jaxrs.templateform;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInsufficientPermission extends LanguagePromptException {

	private static final long serialVersionUID = 1148555249431355284L;

	ExceptionInsufficientPermission(String person) {
		super("사용자 {}에게 이 작업을 수행할 권한이 없습니다.", person);
	}
}
