package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionCategoryInfoNotExists extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCategoryInfoNotExists( String id ) {
		super("지정한 분류 정보가 없습니다. ID: {}", id );
	}
}
