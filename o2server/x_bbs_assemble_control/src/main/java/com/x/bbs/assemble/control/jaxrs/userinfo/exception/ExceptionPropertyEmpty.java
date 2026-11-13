package com.x.bbs.assemble.control.jaxrs.userinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionPropertyEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionPropertyEmpty( String name ) {
		super("입력 데이터가 불완전하여 주제 정보를 저장할 수 없습니다. 속성:" + name );
	}
}
