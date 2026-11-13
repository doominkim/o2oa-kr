package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAppInfoAccessDenied extends LanguagePromptException {
	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionAppInfoAccessDenied() {
		super("게시판 정보는 익명으로 접근할 수 없습니다.");
	}

}
