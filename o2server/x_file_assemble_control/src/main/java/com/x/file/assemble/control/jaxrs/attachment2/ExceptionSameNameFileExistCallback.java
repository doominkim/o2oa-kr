package com.x.file.assemble.control.jaxrs.attachment2;

import com.x.base.core.project.exception.CallbackPromptException;

class ExceptionSameNameFileExistCallback extends CallbackPromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionSameNameFileExistCallback(String callbackName, String fileName) {
		super(callbackName, "같은 이름의 파일이 이미 있습니다: {}.", fileName);
	}
}