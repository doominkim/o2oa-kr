package com.x.organization.assemble.personal.jaxrs.empower;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPersonNotExistWithIdentity extends LanguagePromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	ExceptionPersonNotExistWithIdentity(String identity) {
		super("{}의 사용자를 찾을 수 없습니다.", identity);
	}

}
