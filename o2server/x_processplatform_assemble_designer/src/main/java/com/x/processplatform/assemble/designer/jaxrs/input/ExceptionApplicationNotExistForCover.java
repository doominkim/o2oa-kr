package com.x.processplatform.assemble.designer.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionApplicationNotExistForCover extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionApplicationNotExistForCover(String id, String name, String alias) {
		super("덮어쓸 앱을 찾을 수 없습니다. id: {}, name: {}, alias: {}.", id, name, alias);
	}
}
