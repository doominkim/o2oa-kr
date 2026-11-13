package com.x.base.core.project.exception;

public class ExceptionPersonNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -7354813827434276962L;

	public static String defaultMessage = "사용자가 없습니다: {}.";

	public ExceptionPersonNotExist(String flag) {
		super(defaultMessage, flag);
	}

}
