package com.x.query.service.processing.jaxrs.neural;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionGenerate extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionGenerate(String model) {
		super("신경망({}) 프로젝트를 생성하는 중입니다.", model);
	}
}
