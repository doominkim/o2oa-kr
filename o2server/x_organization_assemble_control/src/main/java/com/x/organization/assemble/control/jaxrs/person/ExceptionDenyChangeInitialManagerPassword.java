package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDenyChangeInitialManagerPassword extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDenyChangeInitialManagerPassword() {
		super("초기 관리자 비밀번호는 콘솔에서 변경하세요.");
	}
}
