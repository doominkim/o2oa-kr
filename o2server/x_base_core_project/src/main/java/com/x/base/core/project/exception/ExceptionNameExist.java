package com.x.base.core.project.exception;

public class ExceptionNameExist extends LanguagePromptException {

	private static final long serialVersionUID = -283505161497831794L;

	public ExceptionNameExist(String str) {
		super("이름: {}, 이미 있습니다.", str);
	}
}
