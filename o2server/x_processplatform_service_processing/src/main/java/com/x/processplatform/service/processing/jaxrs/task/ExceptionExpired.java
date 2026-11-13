package com.x.processplatform.service.processing.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class ExceptionExpired extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionExpired(Exception e, String id, String title, String sequence) {
		super(e, "결재 대기 만료 표시 실패, id: {}, title: {}, sequence: {}.");
	}

}
