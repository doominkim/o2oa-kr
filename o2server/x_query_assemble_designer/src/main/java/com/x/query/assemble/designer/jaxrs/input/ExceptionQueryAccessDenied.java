package com.x.query.assemble.designer.jaxrs.input;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionQueryAccessDenied extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionQueryAccessDenied(String person, String name, String id) {
		super("사용자: {}의 앱 name: {} id: {} 접근 권한이 부족합니다.", person, name, id);
	}

}
