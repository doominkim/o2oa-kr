package com.x.organization.assemble.authentication.jaxrs.zhengwudingding;

import com.x.base.core.project.exception.PromptException;

class ExceptionCanNotFindPerson extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionCanNotFindPerson(String userId) {
		super("정부용 DingTalk 사용자와 연결할 수 없습니다: {}.", userId);
	}
}
