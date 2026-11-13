package com.x.bbs.assemble.control.jaxrs.userinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionPersonNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionPersonNotExists( String name ) {
		super("포럼 분류 주제 분류 typeCategory가 올바르지 않아 조회하거나 저장할 수 없습니다. Name:" + name );
	}
}
