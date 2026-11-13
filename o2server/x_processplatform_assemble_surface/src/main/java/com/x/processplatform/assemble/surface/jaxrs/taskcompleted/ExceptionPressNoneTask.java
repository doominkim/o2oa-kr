package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPressNoneTask extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionPressNoneTask(String id) {
		super("문서: {}에 현재 결재 대기가 없습니다.", id);
	}
}
