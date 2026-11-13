package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSizeFormat extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSizeFormat( String size ) {
		super("요청 매개변수 size 형식이 올바르지 않습니다. 숫자여야 합니다. Size:" + size );
	}
}
