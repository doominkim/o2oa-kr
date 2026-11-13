package com.x.file.assemble.control.jaxrs.attachment2;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttachmentNotExist extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionAttachmentNotExist(String id) {
		super("지정한 파일: {}이(가) 없습니다.", id);
	}
	ExceptionAttachmentNotExist(String id,String fileId) {
		super("지정한 파일: {}의 첨부파일 {}이(가) 없습니다.", id, fileId);
	}
}
