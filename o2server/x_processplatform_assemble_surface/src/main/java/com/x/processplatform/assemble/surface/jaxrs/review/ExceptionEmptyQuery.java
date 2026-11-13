package com.x.processplatform.assemble.surface.jaxrs.review;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyQuery extends LanguagePromptException {

	private static final long serialVersionUID = 8891390969539772549L;

	ExceptionEmptyQuery() {
		super("검색어는 비워 둘 수 없습니다.");
	}
}
