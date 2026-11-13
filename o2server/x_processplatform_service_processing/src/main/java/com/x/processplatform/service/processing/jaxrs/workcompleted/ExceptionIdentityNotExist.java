package com.x.processplatform.service.processing.jaxrs.workcompleted;

import com.x.base.core.project.exception.PromptException;

class ExceptionIdentityNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionIdentityNotExist(String str) {
		super("소속이 없습니다: {}", str);
	}
}
