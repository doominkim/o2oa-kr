package com.x.program.center.jaxrs.collect;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionProxyEmpty extends LanguagePromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionProxyEmpty(String message) {
		super("{} , 비워 둘 수 없습니다.", message );
	}
}
