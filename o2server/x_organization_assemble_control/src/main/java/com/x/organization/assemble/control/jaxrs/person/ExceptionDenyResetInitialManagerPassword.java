package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDenyResetInitialManagerPassword extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDenyResetInitialManagerPassword() {
		super("초기 관리자 비밀번호는 재설정할 수 없습니다.");
	}
}
