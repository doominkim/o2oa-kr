package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionVoteOptionEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionVoteOptionEmpty( ) {
		super("투표 게시물에 투표 항목이 없어 게시할 수 없습니다.");
	}
}
