package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDisableCode extends LanguagePromptException {

	private static final long serialVersionUID = 6351023802034208595L;

	ExceptionDisableCode() {
		super("휴대폰 인증으로 사용자를 등록할 수 없도록 설정되어 있습니다.");
	}
}
