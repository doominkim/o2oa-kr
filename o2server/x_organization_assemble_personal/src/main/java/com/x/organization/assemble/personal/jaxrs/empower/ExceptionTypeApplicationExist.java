package com.x.organization.assemble.personal.jaxrs.empower;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionTypeApplicationExist extends LanguagePromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	ExceptionTypeApplicationExist(String identity, String application) {
		super("소속 {}의 지정 앱 {} 권한 위임이 이미 있습니다.", identity, application);
	}

}
