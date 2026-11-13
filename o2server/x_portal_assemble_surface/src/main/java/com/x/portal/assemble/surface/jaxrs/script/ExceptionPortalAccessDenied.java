package com.x.portal.assemble.surface.jaxrs.script;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPortalAccessDenied extends LanguagePromptException {

	private static final long serialVersionUID = -4908883340253465376L;

	ExceptionPortalAccessDenied(String person, String name, String id) {
		super("사용자: {}의 포털: {}, id: {} 접근 실패, 권한이 부족합니다.", person, name, id);
	}

}
