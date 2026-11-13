package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNoneIdentity extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionNoneIdentity(String person) {
		super("지정한 사용자의 소속을 찾을 수 없습니다: {}.", person);
	}
}
