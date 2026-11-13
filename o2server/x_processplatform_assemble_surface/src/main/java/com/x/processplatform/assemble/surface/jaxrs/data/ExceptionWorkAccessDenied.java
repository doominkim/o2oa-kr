package com.x.processplatform.assemble.surface.jaxrs.data;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionWorkAccessDenied extends LanguagePromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionWorkAccessDenied(String person, String title, String workId) {
		super("사용자: {}에게 문서 title: {} id: {}에 접근할 권한이 없습니다.", person, title,
				workId);
	}
}
