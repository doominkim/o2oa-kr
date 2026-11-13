package com.x.processplatform.service.processing.jaxrs.workcompleted;

import com.x.base.core.project.exception.PromptException;

class ExceptionShiftTime extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionShiftTime(Exception e, String id) {
		super(e, "생성 시간 조정 실패, id: {}.", id);
	}

}
