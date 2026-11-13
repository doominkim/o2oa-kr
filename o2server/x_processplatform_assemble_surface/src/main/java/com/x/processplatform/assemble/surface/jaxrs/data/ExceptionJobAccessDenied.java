package com.x.processplatform.assemble.surface.jaxrs.data;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionJobAccessDenied extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionJobAccessDenied(String person, String str) {
		super("사용자: {}에게 문서 job: {}에 접근할 권한이 없습니다.", person, str);
	}

}
