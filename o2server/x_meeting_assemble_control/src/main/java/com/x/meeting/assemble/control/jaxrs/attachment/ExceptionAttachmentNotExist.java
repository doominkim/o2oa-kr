package com.x.meeting.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttachmentNotExist extends PromptException {

	private static final long serialVersionUID = 7237855733312562652L;

	ExceptionAttachmentNotExist(String id) {
		super("첨부파일: {}이(가) 없습니다.", id);
	}
}
