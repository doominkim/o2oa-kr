package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDenyDeleteInitialManager extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDenyDeleteInitialManager() {
		super("초기 관리자는 삭제할 수 없습니다.");
	}
}
