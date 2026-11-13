package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNotAllowPress extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionNotAllowPress(String activityName) {
		super("{} 단계에서는 독촉할 수 없습니다.", activityName);
	}
}
