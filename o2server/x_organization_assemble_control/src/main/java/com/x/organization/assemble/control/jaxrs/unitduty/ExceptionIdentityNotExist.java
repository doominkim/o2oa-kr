package com.x.organization.assemble.control.jaxrs.unitduty;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionIdentityNotExist extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionIdentityNotExist(String flag) {
		super("소속: {}이(가) 없습니다.", flag);
	}
}
