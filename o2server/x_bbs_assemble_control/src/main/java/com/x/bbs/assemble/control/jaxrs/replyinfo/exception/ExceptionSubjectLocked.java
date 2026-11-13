package com.x.bbs.assemble.control.jaxrs.replyinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSubjectLocked extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSubjectLocked( String id ) {
		super( "주제가 잠겨 있어 댓글을 달 수 없습니다. ID:" + id );
	}
}
