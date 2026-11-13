package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.exception.CallbackPromptException;

class ExceptionAttachmentInvalidCallback extends CallbackPromptException {

	private static final long serialVersionUID = 8275405268546054638L;

	ExceptionAttachmentInvalidCallback(String callbackName, String fileName) {
		super(callbackName, "첨부 파일: {}, 허용되지 않는 업로드 유형입니다.", fileName);
	}

	ExceptionAttachmentInvalidCallback(String callbackName, String fileName, Integer fileSize) {
		super(callbackName, "첨부 파일: {}, 첨부 파일 크기가 제한 {}M을 초과했습니다.", fileName, fileSize);
	}

}
