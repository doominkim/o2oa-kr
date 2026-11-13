package com.x.organization.assemble.control.jaxrs.unitattribute;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNameEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionNameEmpty() {
		super("조직 속성 이름은 비워 둘 수 없습니다.");
	}
}
