package com.x.program.center.jaxrs.invoke;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNotEnable extends LanguagePromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionNotEnable(String name) {
		super("인터페이스: {}이(가) 사용 설정되어 있지 않습니다.", name);
	}
}
