package com.x.message.assemble.communicate.jaxrs.mass;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyBody extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionEmptyBody() {
		super("보낼 내용은 비워 둘 수 없습니다.");
	}
}
