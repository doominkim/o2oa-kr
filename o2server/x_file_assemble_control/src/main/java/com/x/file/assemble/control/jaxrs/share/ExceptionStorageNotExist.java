package com.x.file.assemble.control.jaxrs.share;

import com.x.base.core.project.exception.PromptException;

class ExceptionStorageNotExist extends PromptException {

	private static final long serialVersionUID = 3604314588130414509L;

	ExceptionStorageNotExist(String name) {
		super("스토리지를 찾을 수 없습니다: {}.", name);
	}
}
