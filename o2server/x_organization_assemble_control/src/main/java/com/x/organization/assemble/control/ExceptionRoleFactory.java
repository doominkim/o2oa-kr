package com.x.organization.assemble.control;

import com.x.base.core.project.exception.PromptException;

class ExceptionRoleFactory extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionRoleFactory(Exception e) {
		super(e, "역할 팩토리 객체 초기화에 실패했습니다.");
	}
}
