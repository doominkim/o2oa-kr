package com.x.base.core.project.exception;

import com.x.base.core.entity.JpaObject;

public class ExceptionUnsupportedMediaType extends LanguagePromptException {

	private static final long serialVersionUID = 1051687204485351261L;

	public static String defaultMessage = "지원하지 않는 파일 유형입니다: {}.";

	public <T extends JpaObject> ExceptionUnsupportedMediaType(String type) {
		super(defaultMessage,type);
	}

}
