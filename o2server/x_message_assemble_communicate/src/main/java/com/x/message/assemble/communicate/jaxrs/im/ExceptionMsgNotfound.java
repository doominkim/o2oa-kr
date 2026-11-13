package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionMsgNotfound extends PromptException {


	private static final long serialVersionUID = -7038583339545967795L;

	ExceptionMsgNotfound() {
		super("메시지를 찾을 수 없습니다.");
	}
}
