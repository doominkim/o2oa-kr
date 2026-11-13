package com.x.portal.assemble.designer.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionMenuExistForCreate extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionMenuExistForCreate(String id) {
		super("메뉴 생성 충돌, id: {}.", id);
	}
}
