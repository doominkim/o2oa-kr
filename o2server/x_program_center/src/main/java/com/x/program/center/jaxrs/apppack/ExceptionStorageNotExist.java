package com.x.program.center.jaxrs.apppack;

import com.x.base.core.project.exception.PromptException;

class ExceptionStorageNotExist extends PromptException {


	private static final long serialVersionUID = 8316059112190290401L;

	ExceptionStorageNotExist(String name) {
		super("스토리지를 찾을 수 없습니다: {}.", name);
	}
}
