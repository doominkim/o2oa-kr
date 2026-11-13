package com.x.organization.assemble.control.jaxrs.permissionsetting;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPersonCardNotExist extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionPersonCardNotExist(String flag) {
		super("주소록 설정이 없습니다.", flag);
	}
}
