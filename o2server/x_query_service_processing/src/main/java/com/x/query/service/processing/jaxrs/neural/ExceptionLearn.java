package com.x.query.service.processing.jaxrs.neural;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionLearn extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionLearn(String model) {
		super("신경망({})을 학습하는 중입니다.", model);
	}
}
