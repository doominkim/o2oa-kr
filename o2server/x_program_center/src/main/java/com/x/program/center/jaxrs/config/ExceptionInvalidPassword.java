package com.x.program.center.jaxrs.config;

import com.x.base.core.project.exception.LanguagePromptException;

public class ExceptionInvalidPassword extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionInvalidPassword(String title, String hint) {
		super("{}의 비밀번호가 규칙에 맞지 않습니다: {}.", title, hint);
	}
}
