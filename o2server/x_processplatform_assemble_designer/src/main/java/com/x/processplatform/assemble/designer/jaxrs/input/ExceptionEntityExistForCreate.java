package com.x.processplatform.assemble.designer.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionEntityExistForCreate extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionEntityExistForCreate(String id, Class<?> cls) {
		super("새 엔터티가 충돌합니다. id: {}, class: {}.", id, cls.getName());
	}
}
