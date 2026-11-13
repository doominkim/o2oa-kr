package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSubjectIsNotVoteSubject extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSubjectIsNotVoteSubject( String id ) {
		super( "투표 주제가 아니므로 투표할 수 없습니다. ID:" + id );
	}
}
