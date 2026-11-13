package com.x.file.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.CallbackPromptException;

class ExceptionEmptyExtensionCallback extends CallbackPromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionEmptyExtensionCallback(String callbackName,String name) {
		super(callbackName,"확장자가 없는 파일은 업로드할 수 없습니다: {}.", name);
	}
}
