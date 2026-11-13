package com.x.processplatform.assemble.surface.jaxrs.review;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNotMatchApplication extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionNotMatchApplication(String id, String applicationFlag) {
		super("참조 문서 앱 식별자: {}이(가) 지정한 앱과 일치하지 않습니다: {}.", id, applicationFlag);
	}
}
