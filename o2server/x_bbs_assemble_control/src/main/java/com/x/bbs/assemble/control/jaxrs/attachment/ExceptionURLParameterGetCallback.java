package com.x.bbs.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.CallbackPromptException;

class ExceptionURLParameterGetCallback extends CallbackPromptException {

	private static final long serialVersionUID = 1859164370743532895L;
	
	public ExceptionURLParameterGetCallback( String callbackName, Throwable e ) {
		super(callbackName, "전달된 URL 매개변수를 해석하는 중 오류가 발생했습니다.", e);
	}
}
