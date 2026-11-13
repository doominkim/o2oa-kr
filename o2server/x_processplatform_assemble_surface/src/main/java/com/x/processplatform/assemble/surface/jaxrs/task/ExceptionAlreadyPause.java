package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAlreadyPause extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionAlreadyPause(String id) {
		super("결재 대기가 이미 보류 상태입니다{}.", id);
	}
}
