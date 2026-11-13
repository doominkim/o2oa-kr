package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionRetractNoneTaskCompleted extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionRetractNoneTaskCompleted(String title, String id) {
		super("문서가 이미 처리되어 회수할 수 없습니다, 문서: {}, id: {}.", title, id);
	}
}
