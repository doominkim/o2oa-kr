package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionCategoryInfoAccessDenied extends LanguagePromptException {
	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCategoryInfoAccessDenied() {
		super("분류 정보는 익명으로 접근할 수 없습니다.");
	}
}
