package com.x.program.center.jaxrs.config;

import com.x.base.core.project.exception.LanguagePromptException;

public class ExceptionEmptyCredential extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionEmptyCredential() {
		super("사용자 식별자는 비워 둘 수 없습니다.");
	}
}
