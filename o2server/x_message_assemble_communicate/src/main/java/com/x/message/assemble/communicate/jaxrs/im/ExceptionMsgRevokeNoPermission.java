package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionMsgRevokeNoPermission extends PromptException {


	private static final long serialVersionUID = 126285286239527769L;

	ExceptionMsgRevokeNoPermission() {
		super("현재 사용자는 메시지를 회수할 권한이 없습니다");
	}


}
