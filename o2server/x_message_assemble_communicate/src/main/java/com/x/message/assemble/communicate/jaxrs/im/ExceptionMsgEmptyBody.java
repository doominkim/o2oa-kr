package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionMsgEmptyBody extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionMsgEmptyBody() {
		super("메시지 내용은 비워 둘 수 없습니다.");
	}
}
