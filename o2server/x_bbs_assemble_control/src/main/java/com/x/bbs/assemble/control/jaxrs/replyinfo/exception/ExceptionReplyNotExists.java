package com.x.bbs.assemble.control.jaxrs.replyinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionReplyNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionReplyNotExists( String id ) {
		super("지정한 ID의 댓글 정보가 없습니다. ID:" + id );
	}
}
