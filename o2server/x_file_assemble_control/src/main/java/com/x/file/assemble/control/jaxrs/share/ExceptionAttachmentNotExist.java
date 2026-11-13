package com.x.file.assemble.control.jaxrs.share;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttachmentNotExist extends PromptException {

	private static final long serialVersionUID = 6214553784762782200L;

	ExceptionAttachmentNotExist(String id) {
		super("공유한 파일: {}이(가) 없습니다.", id);
	}
	ExceptionAttachmentNotExist(String id,String fileId) {
		super("공유한 파일: {}의 첨부파일 {}이(가) 없습니다.", id, fileId);
	}
}
