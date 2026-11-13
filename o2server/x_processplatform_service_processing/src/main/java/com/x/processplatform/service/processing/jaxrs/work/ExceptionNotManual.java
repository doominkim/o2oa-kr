package com.x.processplatform.service.processing.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionNotManual extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionNotManual(String activityId) {
		super("사람 처리 단계가 아닙니다: {}.", activityId);
	}

}
