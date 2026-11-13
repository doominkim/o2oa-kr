package com.x.cms.assemble.control.jaxrs.comment;

import com.x.base.core.project.exception.PromptException;

class ExceptionCommentPersist extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCommentPersist( Throwable e, String message ) {
		super("댓글 정보를 저장하는 중 오류가 발생했습니다. Message:" + message, e );
	}

}
