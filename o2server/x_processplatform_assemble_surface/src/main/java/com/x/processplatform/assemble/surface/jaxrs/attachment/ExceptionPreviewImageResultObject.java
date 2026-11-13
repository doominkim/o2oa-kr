package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPreviewImageResultObject extends LanguagePromptException {

	private static final long serialVersionUID = 9085364457175859374L;

	ExceptionPreviewImageResultObject(String flag) {
		super("미리보기 객체가 없습니다: {}.", flag);
	}

}
