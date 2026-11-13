package com.x.program.center.jaxrs.input;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAppInfoAccessDenied extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionAppInfoAccessDenied(String person, String name, String id) {
		super("사용자: {}의 앱 이름: {} id: {} 접근이 거부되었습니다.", person, name, id);
	}

}
