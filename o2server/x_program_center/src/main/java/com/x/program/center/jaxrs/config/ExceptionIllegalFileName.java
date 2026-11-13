package com.x.program.center.jaxrs.config;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionIllegalFileName extends LanguagePromptException {

	private static final long serialVersionUID = -8324509965998709044L;

	public ExceptionIllegalFileName(String name) {
		super("유효하지 않은 파일 이름: {}.", name);
	}

}
