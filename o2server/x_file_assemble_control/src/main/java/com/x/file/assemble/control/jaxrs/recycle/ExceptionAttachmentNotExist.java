package com.x.file.assemble.control.jaxrs.recycle;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttachmentNotExist extends PromptException {

	private static final long serialVersionUID = 6214553784762782200L;

	ExceptionAttachmentNotExist(String id) {
		super("휴지통의 파일: {}이(가) 없습니다.", id);
	}
}
