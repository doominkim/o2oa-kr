package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionIdentityNotInMatrix extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionIdentityNotInMatrix(String identity) {
		super("소속: {}이(가) 결재 대기 소속에 없습니다.");
	}
}
