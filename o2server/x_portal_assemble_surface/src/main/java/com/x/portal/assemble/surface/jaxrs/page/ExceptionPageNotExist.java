package com.x.portal.assemble.surface.jaxrs.page;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPageNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -4908883340253465376L;

	ExceptionPageNotExist(String id) {
		super("지정한 페이지가 없습니다: {}.", id);
	}

}
