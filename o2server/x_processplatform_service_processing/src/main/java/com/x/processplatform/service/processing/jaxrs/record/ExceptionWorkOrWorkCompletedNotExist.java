package com.x.processplatform.service.processing.jaxrs.record;

import com.x.base.core.project.exception.PromptException;

class ExceptionWorkOrWorkCompletedNotExist extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionWorkOrWorkCompletedNotExist(String job) {
		super("문서 또는 완료된 문서가 없습니다, job: {}.", job);
	}

}
