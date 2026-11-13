package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionImportViewIdEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionImportViewIdEmpty() {
		super("분류에 가져오기 목록이 연결되지 않아 가져올 수 없습니다." );
	}
}
