package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionConversationTypeError extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionConversationTypeError() {
		super("대화방 유형이 비어 있거나 올바르지 않습니다.");
	}
}
