package com.x.file.assemble.control.jaxrs.attachment2;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyExtension extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionEmptyExtension(String name) {
		super("확장자가 없는 파일은 업로드할 수 없습니다: {}.", name);
	}

	ExceptionEmptyExtension(String message, String name) {
		super(message, name);
	}

}
