package com.x.base.core.project.exception;

public class ExceptionStorageMappingNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -7354813827434276962L;

	public static final String defaultMessage = "식별자: {} 인 스토리지가 없습니다.";

	public ExceptionStorageMappingNotExist(String storage) {
		super(defaultMessage, storage);
	}

}
