package com.x.processplatform.service.processing.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidArrivedWorkLog extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionInvalidArrivedWorkLog(String work) {
		super("지정한 도착 문서 로그를 찾을 수 없습니다, 문서: {}.");
	}
}
