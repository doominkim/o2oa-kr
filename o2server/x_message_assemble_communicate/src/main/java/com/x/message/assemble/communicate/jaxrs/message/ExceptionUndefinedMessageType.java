package com.x.message.assemble.communicate.jaxrs.message;

import com.x.base.core.project.exception.PromptException;

class ExceptionUndefinedMessageType extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionUndefinedMessageType(String type) {
		super("정의되지 않은 메시지 유형: {}.", type);
	}
}
