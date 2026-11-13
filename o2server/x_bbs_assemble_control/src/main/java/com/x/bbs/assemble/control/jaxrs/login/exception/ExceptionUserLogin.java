package com.x.bbs.assemble.control.jaxrs.login.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionUserLogin extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionUserLogin( Throwable e, String name ) {
		super("사용자 로그인 중 오류가 발생했습니다! Person:" + name, e );
	}

	public ExceptionUserLogin( String message ) {
		super(message );
	}
}
