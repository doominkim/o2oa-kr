package com.x.organization.assemble.control.jaxrs.identity;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDuplicateUnique extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDuplicateUnique(String name, String unique) {
		super("소속 {}의 고유 식별자: {}이(가) 기존 식별자와 중복됩니다.", name, unique);
	}
}
