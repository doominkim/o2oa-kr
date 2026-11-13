package com.x.cms.assemble.control.jaxrs.view;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionViewInfoAppIdEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionViewInfoAppIdEmpty() {
		super("게시판 이름 [appId]가 비어 있어 데이터를 저장할 수 없습니다." );
	}
}
