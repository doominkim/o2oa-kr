package com.x.query.assemble.designer.jaxrs.stat;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAccessDenied extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionAccessDenied(String person) {
		super("사용자: {}의 접근이 거부되었습니다.", person);
	}

}
