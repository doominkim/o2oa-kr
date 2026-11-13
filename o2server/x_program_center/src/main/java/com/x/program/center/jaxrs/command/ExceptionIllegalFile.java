package com.x.program.center.jaxrs.command;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionIllegalFile extends LanguagePromptException {

	private static final long serialVersionUID = -3768442480547066081L;

	public ExceptionIllegalFile(String name) {
		super("유효하지 않은 파일: {}.", name);
	}

}
