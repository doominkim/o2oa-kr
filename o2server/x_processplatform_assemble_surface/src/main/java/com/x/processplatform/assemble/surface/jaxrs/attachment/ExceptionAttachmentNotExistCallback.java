package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.exception.CallbackPromptException;

class ExceptionAttachmentNotExistCallback extends CallbackPromptException {

	private static final long serialVersionUID = -3503683561668855227L;

	ExceptionAttachmentNotExistCallback(String callbackName, String attachmentId) {
		super(callbackName, "지정한 첨부 파일이 없습니다: {}.", attachmentId);
	}

}
