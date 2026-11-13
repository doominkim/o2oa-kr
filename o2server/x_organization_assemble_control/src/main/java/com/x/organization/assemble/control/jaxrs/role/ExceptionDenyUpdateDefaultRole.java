package com.x.organization.assemble.control.jaxrs.role;

import com.x.base.core.project.exception.PromptException;

class ExceptionDenyUpdateDefaultRole extends PromptException {

	private static final long serialVersionUID = 5167597034206987929L;

	ExceptionDenyUpdateDefaultRole(String name) {
		super("시스템 기본 역할의 이름은 변경할 수 없습니다: {}.", name);
	}
}
