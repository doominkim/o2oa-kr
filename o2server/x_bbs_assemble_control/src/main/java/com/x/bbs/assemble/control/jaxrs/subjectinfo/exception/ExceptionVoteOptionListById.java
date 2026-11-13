package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionVoteOptionListById extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionVoteOptionListById( Throwable e, String id ) {
		super("ID로 주제의 투표 항목을 조회하는 중 오류가 발생했습니다. ID:" + id, e );
	}
}
