package com.x.organization.assemble.personal.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidMobile extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public static String defaultMessage = "휴대폰 번호 {} 오류: 비워 둘 수 없으며 지정된 형식이어야 합니다.";

	ExceptionInvalidMobile(String mobile) {
		super(defaultMessage, mobile);
	}
}
