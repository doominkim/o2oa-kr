package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionConvDeleteNoPermission extends PromptException {


	private static final long serialVersionUID = 6058719120644397872L;

	ExceptionConvDeleteNoPermission() {
		super("대화방을 삭제할 권한이 없습니다!");
	}


}
