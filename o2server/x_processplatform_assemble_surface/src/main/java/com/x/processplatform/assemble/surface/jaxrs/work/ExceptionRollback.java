package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionRollback extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionRollback(String work) {
		super("문서: {} 롤백에 실패했습니다.", work);
	}
}
