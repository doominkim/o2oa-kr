package com.x.cms.assemble.control.jaxrs.comment;

import com.x.base.core.project.exception.LanguagePromptException;

class CommentQueryException extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CommentQueryException( Throwable e, String message ) {
		super("댓글 정보를 조회하는 중 오류가 발생했습니다. Message:" + message, e );
	}
}
