package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidCaptcha extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionInvalidCaptcha() {
		super("이미지 인증 코드가 올바르지 않습니다.");
	}
}
