package com.x.cms.assemble.control.jaxrs.comment;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionCommentIdForQueryEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCommentIdForQueryEmpty() {
		super("조회할 댓글 ID가 비어 있어 데이터를 조회할 수 없습니다." );
	}
}
