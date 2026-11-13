package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionEntityPropertyEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionEntityPropertyEmpty(String message) {
		super("정보 속성이 비어 있습니다! MESSAGE:" + message );
	}
}
