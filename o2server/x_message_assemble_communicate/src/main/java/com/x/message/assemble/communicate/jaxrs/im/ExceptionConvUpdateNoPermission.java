package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionConvUpdateNoPermission extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionConvUpdateNoPermission() {
		super("대화방 속성을 수정할 권한이 없습니다");
	}


}
