package com.x.file.assemble.control.jaxrs.attachment2;

import com.x.base.core.project.exception.CallbackPromptException;

class ExceptionExtensionNotMatchCallback extends CallbackPromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionExtensionNotMatchCallback(String callbackName, String name, String extension) {
		super(callbackName, "파일: {}의 확장자가 일치하지 않습니다. 필요한 확장자: {}.", name);
	}
}
