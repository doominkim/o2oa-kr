package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyOption extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionEmptyOption() {
		super("사용자 소속이 비어 있거나 모두 이미 처리자에 포함되어 있습니다.");
	}

}
