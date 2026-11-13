package com.x.cms.assemble.control.jaxrs.templateform;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidCategory extends LanguagePromptException {

	private static final long serialVersionUID = 6984800093761853101L;

	ExceptionInvalidCategory(String category) {
		super("분류 필드 검증에 실패했습니다: {}.", category);
	}

}
