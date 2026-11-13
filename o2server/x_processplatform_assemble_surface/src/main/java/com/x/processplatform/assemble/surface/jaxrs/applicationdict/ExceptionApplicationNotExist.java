package com.x.processplatform.assemble.surface.jaxrs.applicationdict;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionApplicationNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -5954915325716358186L;

	ExceptionApplicationNotExist(String flag) {
		super("지정한 앱이 없습니다: {}.", flag);
	}

}
