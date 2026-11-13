package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyBusinessObject extends PromptException {


	private static final long serialVersionUID = 1486583753209029281L;

	ExceptionEmptyBusinessObject(String businessId) {
		super("해당 업무 객체를 조회할 수 없습니다! businessId: " + businessId);
	}


}
