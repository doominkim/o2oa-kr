package com.x.organization.assemble.control.jaxrs.unit;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNameInvalid extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionNameInvalid(String name) {
		super("조직 {}의 이름이 요구 사항에 맞지 않습니다.", name);
	}
}
