package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAppInfoIdEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionAppInfoIdEmpty() {
		super("게시판 ID가 비어 있어 데이터를 조회할 수 없습니다." );
	}
}
