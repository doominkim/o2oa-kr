package com.x.program.center.jaxrs.input;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAppInfoNotExistForCover extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionAppInfoNotExistForCover(String id, String name, String alias) {
		super("덮어쓸 앱을 찾을 수 없습니다. id: {}, name: {}, alias: {}.", id, name, alias);
	}
}
