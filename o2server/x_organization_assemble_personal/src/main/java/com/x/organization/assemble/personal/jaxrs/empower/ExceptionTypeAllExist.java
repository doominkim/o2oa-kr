package com.x.organization.assemble.personal.jaxrs.empower;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionTypeAllExist extends LanguagePromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	ExceptionTypeAllExist(String identity) {
		super("소속 {}의 전체 설정이 이미 있습니다.", identity);
	}

}
