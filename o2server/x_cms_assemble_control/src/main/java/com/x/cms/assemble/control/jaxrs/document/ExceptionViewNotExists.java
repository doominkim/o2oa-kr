package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionViewNotExists extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionViewNotExists( String id ) {
		super("데이터 뷰가 없어 작업을 계속할 수 없습니다. Id: {}", id );
	}
}
