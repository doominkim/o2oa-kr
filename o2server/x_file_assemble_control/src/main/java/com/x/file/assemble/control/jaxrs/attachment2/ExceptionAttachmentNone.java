package com.x.file.assemble.control.jaxrs.attachment2;


import com.x.base.core.project.exception.PromptException;

class ExceptionAttachmentNone extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionAttachmentNone(String name) {
		super("업로드된 첨부파일이 없습니다: {}.", name);
	}
}
