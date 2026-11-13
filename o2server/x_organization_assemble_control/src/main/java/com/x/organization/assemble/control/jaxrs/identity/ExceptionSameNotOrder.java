package com.x.organization.assemble.control.jaxrs.identity;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionSameNotOrder extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionSameNotOrder(String flag, String otherFlag) {
		super("같은 소속끼리는 정렬할 수 없습니다: {}, : {}.", flag, otherFlag);
	}
}
