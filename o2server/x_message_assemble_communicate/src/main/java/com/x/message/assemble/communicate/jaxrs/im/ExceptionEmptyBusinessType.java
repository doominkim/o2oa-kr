package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyBusinessType extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionEmptyBusinessType() {
		super("businessType이 비어 있거나 내용이 잘못되었습니다!");
	}


}
