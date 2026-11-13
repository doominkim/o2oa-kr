package com.x.processplatform.service.processing.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidFromWorkLog extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionInvalidFromWorkLog(String work) {
		super("지정한 출발 문서 로그를 찾을 수 없습니다, 문서: {}.");
	}
}
