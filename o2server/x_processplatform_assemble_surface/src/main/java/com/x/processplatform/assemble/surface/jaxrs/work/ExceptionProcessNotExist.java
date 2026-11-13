package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionProcessNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionProcessNotExist(String flag) {
		super("지정한 결재가 없습니다: {}.", flag);
	}
}
