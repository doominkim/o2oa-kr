package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPressNoneTaskCompleted extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionPressNoneTaskCompleted(String workId, String person) {
		super("독촉에 사용할 현재 사용자의 결재 완료를 찾을 수 없습니다, 문서 식별자: {}, 현재 사용자: {}.", workId, person);
	}
}
