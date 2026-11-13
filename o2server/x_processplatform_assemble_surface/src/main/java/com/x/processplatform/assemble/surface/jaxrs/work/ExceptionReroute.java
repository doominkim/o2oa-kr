package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionReroute extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionReroute(String workId) {
		super("단계 이동 실패, 문서: {}.", workId);
	}
}
