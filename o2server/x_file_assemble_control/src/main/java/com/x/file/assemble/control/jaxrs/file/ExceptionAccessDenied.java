package com.x.file.assemble.control.jaxrs.file;

import com.x.base.core.project.exception.PromptException;

class ExceptionAccessDenied extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionAccessDenied(String person) {
		super("사용자: {}, 권한이 없어 접근이 거부되었습니다.", person);
	}
}
