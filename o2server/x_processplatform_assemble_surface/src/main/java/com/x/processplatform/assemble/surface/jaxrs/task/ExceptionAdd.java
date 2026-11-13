package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAdd extends LanguagePromptException {

	private static final long serialVersionUID = -1678718913437722938L;

	ExceptionAdd(String id) {
		super("결재 대기자 추가 실패, task: {}.", id);
	}

}