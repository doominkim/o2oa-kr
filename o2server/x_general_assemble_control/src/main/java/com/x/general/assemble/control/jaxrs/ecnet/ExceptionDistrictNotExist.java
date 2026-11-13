package com.x.general.assemble.control.jaxrs.ecnet;

import com.x.base.core.project.exception.PromptException;

class ExceptionDistrictNotExist extends PromptException {

	private static final long serialVersionUID = 7237855733312562652L;

	ExceptionDistrictNotExist(String name) {
		super("지정한 지역이 없습니다: {}.", name);
	}
}
