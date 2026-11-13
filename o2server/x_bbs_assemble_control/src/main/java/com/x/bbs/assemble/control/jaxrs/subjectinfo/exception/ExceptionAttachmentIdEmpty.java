package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionAttachmentIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionAttachmentIdEmpty() {
		super("첨부파일 ID가 비어 있어 조회할 수 없습니다." );
	}
}
