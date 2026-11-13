package com.x.query.assemble.surface.jaxrs.neural;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionProjectNotReady extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionProjectNotReady(String projectName) {
		super("신경망 다층 퍼셉트론({})이 아직 준비되지 않았습니다");
	}
}
