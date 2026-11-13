package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.project.exception.LanguagePromptException;

public class ExceptionInvalidCode extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionInvalidCode() {
		super("휴대폰 인증 코드가 올바르지 않습니다.");
	}
}
