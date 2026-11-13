package com.x.organization.assemble.personal.jaxrs.password;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNewPasswordSameAsOldPassword extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionNewPasswordSameAsOldPassword() {
		super("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
	}
}
