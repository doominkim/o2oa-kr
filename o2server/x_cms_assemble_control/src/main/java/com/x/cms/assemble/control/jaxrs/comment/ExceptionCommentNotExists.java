package com.x.cms.assemble.control.jaxrs.comment;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionCommentNotExists extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCommentNotExists( String id ) {
		super("지정한 ID의 댓글 정보가 없습니다. ID: {}.", id );
	}
}
