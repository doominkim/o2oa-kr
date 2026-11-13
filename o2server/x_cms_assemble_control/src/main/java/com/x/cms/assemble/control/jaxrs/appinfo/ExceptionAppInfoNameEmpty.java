package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAppInfoNameEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionAppInfoNameEmpty() {
		super("게시판 이름 AppName이 비어 있어 저장할 수 없습니다." );
	}
}
