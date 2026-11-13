package com.x.bbs.assemble.control.jaxrs.replyinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionPageEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionPageEmpty() {
		super("조회할 페이지 번호 page가 비어 있어 조회할 수 없습니다." );
	}
}
