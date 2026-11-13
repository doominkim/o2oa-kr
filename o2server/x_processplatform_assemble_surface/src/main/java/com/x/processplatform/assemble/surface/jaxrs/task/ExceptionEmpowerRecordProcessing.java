package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmpowerRecordProcessing extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionEmpowerRecordProcessing(String job) {
		super("권한 위임 기록 실패, job: {}.", job);
	}

}
