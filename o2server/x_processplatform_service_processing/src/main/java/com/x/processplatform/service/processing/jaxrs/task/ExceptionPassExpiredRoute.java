package com.x.processplatform.service.processing.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class ExceptionPassExpiredRoute extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionPassExpiredRoute(String id, String title, String sequence) {
		super("결재 대기 만료 시 기본 경로를 찾을 수 없습니다, id: {}, title: {}, sequence: {}.", id, title, sequence);
	}
}
