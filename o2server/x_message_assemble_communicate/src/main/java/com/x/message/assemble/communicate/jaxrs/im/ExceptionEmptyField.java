package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyField extends PromptException {


	private static final long serialVersionUID = 6866678997540423228L;

	ExceptionEmptyField(String fieldName) {
		super( fieldName+" 은(는) 비워 둘 수 없습니다");
	}


}
