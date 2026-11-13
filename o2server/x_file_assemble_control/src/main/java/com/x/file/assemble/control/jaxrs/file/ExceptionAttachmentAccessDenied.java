package com.x.file.assemble.control.jaxrs.file;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttachmentAccessDenied extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionAttachmentAccessDenied(String person, String name, String id) {
		super("사용자: {}의 Attachment: {}, id: {} 접근이 권한 부족으로 거부되었습니다.", person, name, id);
	}
}
