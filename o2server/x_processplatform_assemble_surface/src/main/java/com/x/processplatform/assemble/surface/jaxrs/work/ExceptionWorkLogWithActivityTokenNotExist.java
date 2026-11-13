package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionWorkLogWithActivityTokenNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionWorkLogWithActivityTokenNotExist(String str) {
		super("활동 토큰으로 문서 로그를 가져올 수 없습니다: {}.", str);
	}
}
