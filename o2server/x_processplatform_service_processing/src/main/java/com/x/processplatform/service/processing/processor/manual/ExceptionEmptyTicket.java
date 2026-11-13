package com.x.processplatform.service.processing.processor.manual;

import com.x.base.core.project.exception.RunningException;

class ExceptionEmptyTicket extends RunningException {

	private static final long serialVersionUID = 9085364457175859374L;

	ExceptionEmptyTicket(String title, String id, String activityName, String activityId) {
		super("예상 처리자가 비어 있습니다, 제목: {}, 식별자: {}, 단계: {}, 단계 식별자: {}.", title, id, activityName, activityId);
	}

}
