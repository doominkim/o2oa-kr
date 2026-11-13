package com.x.base.core.project.exception;

public class ExceptionFieldEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 8181296584823275140L;

	public static String defaultMessage = "매개변수: {} 값이 올바르지 않습니다.";

	public ExceptionFieldEmpty(String field) {
		super(defaultMessage, field);
	}

}
