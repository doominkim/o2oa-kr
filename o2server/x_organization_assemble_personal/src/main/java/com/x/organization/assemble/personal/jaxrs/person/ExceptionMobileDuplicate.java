package com.x.organization.assemble.personal.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

 class ExceptionMobileDuplicate extends LanguagePromptException {

	private static final long serialVersionUID = 4433998001143598936L;

	 public static String defaultMessage = "휴대폰 번호 오류: {}, {}이(가) 기존 값과 중복됩니다.";

	 ExceptionMobileDuplicate(String mobile, String fieldName) {
		super(defaultMessage, mobile, fieldName);
	}
}
