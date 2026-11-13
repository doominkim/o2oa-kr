package com.x.processplatform.assemble.surface.jaxrs.read;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyCountFilter extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionEmptyCountFilter() {
		super("결재 대기 건수 필터 조건이 비어 있습니다.");
	}
}
