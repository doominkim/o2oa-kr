package com.x.processplatform.service.processing.jaxrs.record;

import com.x.base.core.project.exception.PromptException;

class ExceptionWorkCompletedNotFoundWorkLog extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionWorkCompletedNotFoundWorkLog(String workCompletedId) {
		super("완료된 문서의 workLog를 찾을 수 없습니다, id: {}.", workCompletedId);
	}

}
