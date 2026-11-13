package com.x.processplatform.assemble.designer.jaxrs.application;

import com.x.base.core.project.exception.LanguagePromptException;

public class ExceptionInsufficientPermission extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionInsufficientPermission(String person) {
		super("사용자: {}에게 이 작업을 수행할 권한이 없습니다.", person);
	}
}
