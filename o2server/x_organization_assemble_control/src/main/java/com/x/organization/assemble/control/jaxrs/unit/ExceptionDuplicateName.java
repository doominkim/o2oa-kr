package com.x.organization.assemble.control.jaxrs.unit;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDuplicateName extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDuplicateName(String name) {
		super("조직 {}은(는) 같은 계층 안에서 고유해야 합니다.", name);
	}
}
