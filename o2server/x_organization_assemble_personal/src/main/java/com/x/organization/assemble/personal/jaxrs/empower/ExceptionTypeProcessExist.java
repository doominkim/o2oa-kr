package com.x.organization.assemble.personal.jaxrs.empower;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionTypeProcessExist extends LanguagePromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	ExceptionTypeProcessExist(String identity, String process) {
		super("소속 {}의 지정 결재 {} 권한 위임이 이미 있습니다.", identity, process);
	}

}
