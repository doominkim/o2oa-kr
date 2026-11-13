package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmployeeDuplicate extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionEmployeeDuplicate(String name, String fieldName) {
		super("사용자 이메일 오류: {}, {}이(가) 기존 값과 중복됩니다.", name, fieldName);
	}
}
