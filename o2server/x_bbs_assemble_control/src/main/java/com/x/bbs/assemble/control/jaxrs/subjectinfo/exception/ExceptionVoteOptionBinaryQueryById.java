package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionVoteOptionBinaryQueryById extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionVoteOptionBinaryQueryById( Throwable e, String id ) {
		super("항목 ID로 항목의 바이너리 내용을 조회하는 중 오류가 발생했습니다. ID:" + id, e );
	}
}
