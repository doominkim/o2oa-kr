package com.x.query.assemble.designer.jaxrs.neural;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEntryEmpty extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionEntryEmpty(String model) {
		super("신경망 다층 퍼셉트론({}) 프로젝트의 학습 데이터 세트가 비어 있습니다.", model);
	}
}
