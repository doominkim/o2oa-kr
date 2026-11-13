package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidUnit extends PromptException {

	private static final long serialVersionUID = 6647381353782082070L;

	ExceptionInvalidUnit(String unit) {
		super("지정한 조직이 없습니다: {}.", unit);
	}
}
