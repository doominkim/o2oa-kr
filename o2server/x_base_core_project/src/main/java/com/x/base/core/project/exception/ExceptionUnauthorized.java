package com.x.base.core.project.exception;

public class ExceptionUnauthorized extends LanguagePromptException {

	private static final long serialVersionUID = 8121998765154409958L;

	public ExceptionUnauthorized() {
		super("세션이 만료되었거나 로그인하지 않았습니다.");
	}
}
