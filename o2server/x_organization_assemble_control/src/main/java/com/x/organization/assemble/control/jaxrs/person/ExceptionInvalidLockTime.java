package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidLockTime extends PromptException {

	private static final long serialVersionUID = -551429161489167527L;

	ExceptionInvalidLockTime() {
		super("잠금 만료 시간은 현재 시간보다 늦어야 합니다.");
	}
}
