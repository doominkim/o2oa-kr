package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionVoteResultQueryById extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionVoteResultQueryById( Throwable e, String id ) {
		super("지정한 ID의 주제 투표 결과를 조회하는 중 오류가 발생했습니다. ID:" + id, e );
	}
}
