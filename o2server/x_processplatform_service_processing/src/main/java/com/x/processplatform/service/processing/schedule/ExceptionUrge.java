package com.x.processplatform.service.processing.schedule;

import com.x.base.core.project.exception.PromptException;

class ExceptionUrge extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionUrge(Exception e, String id, String title, String sequence) {
		super(e, "결재 대기 독촉 실패, id: {}, title: {}, sequence: {}.");
	}

}
