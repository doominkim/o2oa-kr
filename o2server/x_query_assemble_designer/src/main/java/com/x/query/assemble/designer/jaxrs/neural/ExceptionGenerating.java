package com.x.query.assemble.designer.jaxrs.neural;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionGenerating extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionGenerating(String model) {
		super("신경망 다층 퍼셉트론({}) 프로젝트를 생성하는 중입니다.", model);
	}
}
