package com.x.organization.assemble.personal.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEditInitialManagerDeny extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	public static String defaultMessage = "초기 관리자는 수정할 수 없습니다.";

	ExceptionEditInitialManagerDeny() {
		super(defaultMessage);
	}
}
