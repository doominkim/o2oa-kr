package com.x.processplatform.service.processing.jaxrs.event;

import com.x.base.core.project.exception.PromptException;

class ExceptionJobEmpty extends PromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionJobEmpty() {
		super("job은 비워 둘 수 없습니다.");
	}
}
