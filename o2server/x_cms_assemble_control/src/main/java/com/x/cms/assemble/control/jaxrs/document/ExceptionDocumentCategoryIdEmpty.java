package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDocumentCategoryIdEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionDocumentCategoryIdEmpty() {
		super("게시글 분류 ID가 비어 있어 작업할 수 없습니다." );
	}
}
