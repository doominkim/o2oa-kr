package com.x.file.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;

class ExceptionSameNameFileExist extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionSameNameFileExist(String fileName) {
		super("같은 이름의 파일이 이미 있습니다: {}.", fileName);
	}
}
