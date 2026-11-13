package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNotFoundTaskOrTaskCompleted extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionNotFoundTaskOrTaskCompleted(String flag) {
		super("결재 대기 또는 결재 완료를 찾을 수 없습니다, 식별자: {}.", flag);
	}
}
