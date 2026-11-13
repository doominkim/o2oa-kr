package com.x.query.service.processing.jaxrs.neural;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionModelNotReady extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionModelNotReady(String model) {
		super("신경망({})이 아직 준비되지 않았습니다.", model);
	}
}
