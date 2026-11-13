package com.x.processplatform.assemble.surface.schedule;

import com.x.base.core.project.exception.PromptException;

class ExceptionPassExpired extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionPassExpired(Exception e, String id, String title) {
		super(e, "기한 초과 문서의 기본 경로 진행 실패, id: {}, title: {}.", id, title);
	}

}
