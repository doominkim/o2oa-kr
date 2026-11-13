package com.x.bbs.assemble.control.jaxrs.replyinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionReplyContentEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionReplyContentEmpty() {
		super("댓글 내용이 비어 있어 저장할 수 없습니다." );
	}
}
