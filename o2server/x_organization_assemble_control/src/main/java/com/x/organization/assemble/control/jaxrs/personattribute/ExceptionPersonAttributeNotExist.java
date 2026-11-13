package com.x.organization.assemble.control.jaxrs.personattribute;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPersonAttributeNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionPersonAttributeNotExist(String name) {
		super("개인 속성: {}이(가) 없습니다.", name);
	}
}
