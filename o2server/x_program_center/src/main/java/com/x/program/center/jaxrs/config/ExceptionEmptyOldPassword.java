package com.x.program.center.jaxrs.config;

import com.x.base.core.project.exception.LanguagePromptException;

public class ExceptionEmptyOldPassword extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionEmptyOldPassword() {
		super("기존 비밀번호는 비워 둘 수 없습니다.");
	}
}
