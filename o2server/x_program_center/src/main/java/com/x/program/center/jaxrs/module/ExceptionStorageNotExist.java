package com.x.program.center.jaxrs.module;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionStorageNotExist extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionStorageNotExist(String name) {
		super("스토리지를 찾을 수 없습니다: {}.", name);
	}
}
