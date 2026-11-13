package com.x.organization.assemble.control.jaxrs.unitduty;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDuplicateOnUnit extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDuplicateOnUnit(String name, String unitName) {
		super("조직 직무 이름 {}은(는) 같은 조직 {} 안에서 고유해야 합니다.", name, unitName);
	}
}
