package com.x.query.assemble.designer.jaxrs.designer;

import com.x.base.core.project.exception.LanguagePromptException;

public class ExceptionFieldEmpty extends LanguagePromptException {

	public ExceptionFieldEmpty(String field) {
		super("매개변수: {} 값이 올바르지 않습니다.", field);
	}

}
