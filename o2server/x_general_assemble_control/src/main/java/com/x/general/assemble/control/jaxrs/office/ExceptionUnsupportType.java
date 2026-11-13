package com.x.general.assemble.control.jaxrs.office;

import com.x.base.core.project.exception.PromptException;

class ExceptionUnsupportType extends PromptException {

	private static final long serialVersionUID = 7237855733312562652L;

	ExceptionUnsupportType(String type) {
		super("지원하지 않는 파일 형식: {}.", type);
	}
}
