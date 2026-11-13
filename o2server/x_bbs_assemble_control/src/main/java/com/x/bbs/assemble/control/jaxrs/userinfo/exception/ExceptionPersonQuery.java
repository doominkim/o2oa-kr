package com.x.bbs.assemble.control.jaxrs.userinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionPersonQuery extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionPersonQuery( Throwable e, String name ) {
		super("인원 고유 식별자로 인원 정보를 조회하는 중 오류가 발생했습니다. Name:" + name,e );
	}
}
