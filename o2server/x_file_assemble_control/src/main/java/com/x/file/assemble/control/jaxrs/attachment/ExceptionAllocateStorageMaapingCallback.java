package com.x.file.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.CallbackPromptException;

class ExceptionAllocateStorageMaapingCallback extends CallbackPromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionAllocateStorageMaapingCallback(String callbackName) {
		super(callbackName, "스토리지를 할당할 수 없습니다.");
	}
}
