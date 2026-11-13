package com.x.file.assemble.control.jaxrs.file;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttachmentNotExisted extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionAttachmentNotExisted(String id) {
		super("지정한 Attachmenet: {}이(가) 없습니다.", id);
	}
}
