package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionWorkAccessDenied extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionWorkAccessDenied(String person, String title, String id) {
		super("사용자: {}에게 문서 title: {}, id: {}에 접근할 권한이 없습니다.", person, title, id);
	}

}
