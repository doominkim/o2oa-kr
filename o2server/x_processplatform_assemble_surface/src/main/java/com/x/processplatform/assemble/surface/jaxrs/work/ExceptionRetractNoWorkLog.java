package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionRetractNoWorkLog extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionRetractNoWorkLog(String workId) {
		super("회수 위치를 찾을 수 없습니다, 문서: {}.", workId);
	}
}
