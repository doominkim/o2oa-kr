package com.x.file.assemble.control.jaxrs.recycle;

import com.x.base.core.project.exception.PromptException;

class ExceptionShareNameEmpty extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionShareNameEmpty() {
		super("공유할 파일은 비워 둘 수 없습니다.");
	}
}
