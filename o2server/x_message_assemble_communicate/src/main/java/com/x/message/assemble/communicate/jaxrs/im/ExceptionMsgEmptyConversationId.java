package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionMsgEmptyConversationId extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionMsgEmptyConversationId() {
		super("대화 ID는 비워 둘 수 없습니다.");
	}
}
