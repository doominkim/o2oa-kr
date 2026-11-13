package com.x.processplatform.service.processing.jaxrs.review;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonEmpty extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionPersonEmpty() {
		super("사용자를 지정하지 않았습니다.");
	}

}
