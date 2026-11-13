package com.x.bbs.assemble.control.jaxrs.replyinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionCountEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;
	
	public ExceptionCountEmpty() {
		super("페이지당 항목 수 count가 비어 있어 조회할 수 없습니다." );
	}
	
}
