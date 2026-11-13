package com.x.cms.assemble.control.jaxrs.appdict;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAppDictNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -5954915325716358186L;

	ExceptionAppDictNotExist(String flag) {
		super("식별자 {}인 데이터 사전이 없습니다.", flag);
	}

}
