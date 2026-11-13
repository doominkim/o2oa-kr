package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyRouteName extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionEmptyRouteName() {
		super("처리 선택은 비워 둘 수 없습니다.");
	}
}
