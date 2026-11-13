package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionSuperiorNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionSuperiorNotExist(String superior) {
		super("지정한 상위 관리자: {}이(가) 없습니다.", superior);
	}
}
