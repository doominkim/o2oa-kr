package com.x.organization.assemble.authentication.jaxrs.qiyeweixin;

import com.x.base.core.project.exception.PromptException;

class ExceptionUrlEmpty extends PromptException {


	private static final long serialVersionUID = 7017033987204155801L;

	ExceptionUrlEmpty() {
		super("url 매개변수는 비워 둘 수 없습니다.");
	}
}
