package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionWorkProcessing extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionWorkProcessing(String id) {
		super("문서 {} 처리에 실패했습니다.", id);
	}

}
