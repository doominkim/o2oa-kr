package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNotAllowPause extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionNotAllowPause(String id) {
		super("결재 대기를 보류할 수 없습니다: {}.", id);
	}

}
