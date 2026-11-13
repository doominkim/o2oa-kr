package com.x.cms.assemble.control.jaxrs.appdict;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAppInfoNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -5954915325716358186L;

	ExceptionAppInfoNotExist(String flag) {
		super("식별자 {}인 앱이 없습니다.", flag);
	}

}
