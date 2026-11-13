package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionReset extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionReset(String id) {
		super("작업: {} 처리자 재지정에 실패했습니다.", id);
	}

}
