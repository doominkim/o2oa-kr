package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionFileNameEmpty extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionFileNameEmpty() {
		super("파일 이름은 비워 둘 수 없습니다");
	}
}
