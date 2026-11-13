package com.x.query.assemble.designer.jaxrs.neural;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionLearning extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionLearning(String model) {
		super("신경망 다층 퍼셉트론({}) 프로젝트를 학습하는 중입니다.", model);
	}
}
