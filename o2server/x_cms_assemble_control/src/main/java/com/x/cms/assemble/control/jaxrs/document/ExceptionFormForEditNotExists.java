package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionFormForEditNotExists extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionFormForEditNotExists( String id ) {
		super("분류에 설정된 게시글 편집 양식이 없습니다. ID: {}", id );
	}
}
