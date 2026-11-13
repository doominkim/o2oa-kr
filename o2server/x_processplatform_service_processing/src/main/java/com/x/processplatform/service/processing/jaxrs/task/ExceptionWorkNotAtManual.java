package com.x.processplatform.service.processing.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class ExceptionWorkNotAtManual extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionWorkNotAtManual(String flag) {
		super("문서: {}이(가) 사람 처리 단계에 있지 않습니다.", flag);
	}
}
