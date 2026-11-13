package com.x.bbs.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;

class ExceptionURLParameterGet extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;
	
	public ExceptionURLParameterGet( Throwable e ) {
		super("전달된 URL 매개변수를 해석하는 중 오류가 발생했습니다.", e);
	}
}
