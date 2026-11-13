package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEndNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionEndNotExist(String str) {
		super("결재: {}의 종료 단계를 찾을 수 없습니다.", str);
	}

}
