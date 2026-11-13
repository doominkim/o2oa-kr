package com.x.processplatform.service.processing.jaxrs.event;

import com.x.base.core.project.exception.PromptException;

class ExceptionTargetEmpty extends PromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionTargetEmpty() {
		super("target은 비워 둘 수 없습니다.");
	}
}
