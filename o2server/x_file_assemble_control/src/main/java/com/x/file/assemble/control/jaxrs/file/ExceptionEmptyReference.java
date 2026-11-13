package com.x.file.assemble.control.jaxrs.file;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyReference extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionEmptyReference(String name) {
		super("참조 값은 비워 둘 수 없습니다: {}.", name);
	}
}
