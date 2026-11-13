package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDocumentNotExists extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionDocumentNotExists( String id ) {
		super("게시글 정보가 없어 작업을 계속할 수 없습니다. id: {}", id );
	}
}
