package com.x.processplatform.service.processing.jaxrs.service;

import com.x.base.core.project.exception.PromptException;

class ExceptionActivityNotService extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionActivityNotService(String id) {
		super("문서: {}이(가) 서비스 단계에 있지 않습니다.", id);
	}

}
