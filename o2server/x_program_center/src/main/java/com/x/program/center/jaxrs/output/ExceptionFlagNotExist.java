package com.x.program.center.jaxrs.output;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionFlagNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionFlagNotExist(String flag) {
		super("다운로드 식별자: {}이(가) 없습니다.", flag);
	}
}
