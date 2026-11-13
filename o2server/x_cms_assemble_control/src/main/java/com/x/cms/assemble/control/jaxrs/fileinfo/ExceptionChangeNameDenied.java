package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionChangeNameDenied extends PromptException {

	private static final long serialVersionUID = 9085364457175859374L;

	ExceptionChangeNameDenied(String name) {
		super("규칙에 맞지 않는 이름: {}.", name);
	}

}
