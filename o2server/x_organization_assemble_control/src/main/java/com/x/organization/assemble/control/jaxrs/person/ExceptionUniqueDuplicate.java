package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

 class ExceptionUniqueDuplicate extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	 ExceptionUniqueDuplicate(String name, String fieldName) {
		super("사용자 고유 코드 오류: {}, {}이(가) 기존 값과 중복됩니다.", name, fieldName);
	}
}
