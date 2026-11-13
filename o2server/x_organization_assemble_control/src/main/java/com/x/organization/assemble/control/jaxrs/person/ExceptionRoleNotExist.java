package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionRoleNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionRoleNotExist(String name) {
		super("역할: {}이(가) 없습니다.", name);
	}
}
