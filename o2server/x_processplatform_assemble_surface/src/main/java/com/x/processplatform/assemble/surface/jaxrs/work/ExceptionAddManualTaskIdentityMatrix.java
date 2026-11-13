package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAddManualTaskIdentityMatrix extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionAddManualTaskIdentityMatrix(String workId) {
		super("결재 대기 소속 추가 오류, work: {}.", workId);
	}
}
