package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.project.exception.LanguagePromptException;

public class ExceptionInvalidPassword extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionInvalidPassword(String hint) {
		super("비밀번호 규칙에 맞지 않습니다: {}.", hint);
	}
}
