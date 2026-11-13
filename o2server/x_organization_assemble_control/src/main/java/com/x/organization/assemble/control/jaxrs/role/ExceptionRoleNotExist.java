package com.x.organization.assemble.control.jaxrs.role;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionRoleNotExist extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionRoleNotExist(String flag) {
		super("역할: {}이(가) 없습니다.", flag);
	}
}
