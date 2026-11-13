package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDocumentIdEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionDocumentIdEmpty() {
		super("게시글 ID가 비어 있어 조회할 수 없습니다." );
	}
}
