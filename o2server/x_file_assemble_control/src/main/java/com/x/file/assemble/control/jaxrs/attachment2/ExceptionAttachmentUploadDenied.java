package com.x.file.assemble.control.jaxrs.attachment2;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttachmentUploadDenied extends PromptException {

	private static final long serialVersionUID = -7797553180316608418L;

	ExceptionAttachmentUploadDenied(String name) {
		super("[{}] 파일 형식이 업로드 조건에 맞지 않습니다", name);
	}
}
