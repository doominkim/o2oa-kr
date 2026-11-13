package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionExtend extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionExtend(String id) {
		super("결재 대기자 확장 실패, task: {}.", id);
	}

}
