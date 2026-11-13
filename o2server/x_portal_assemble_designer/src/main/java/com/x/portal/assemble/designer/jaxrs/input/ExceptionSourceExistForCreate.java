package com.x.portal.assemble.designer.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionSourceExistForCreate extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionSourceExistForCreate(String id) {
		super("리소스 생성 충돌, id: {}.");
	}
}
