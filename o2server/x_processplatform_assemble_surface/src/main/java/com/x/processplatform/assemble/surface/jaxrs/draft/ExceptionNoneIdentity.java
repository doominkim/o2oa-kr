package com.x.processplatform.assemble.surface.jaxrs.draft;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNoneIdentity extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionNoneIdentity(String person) {
		super("사용자 소속을 확인할 수 없습니다: {}.", person);
	}
}
