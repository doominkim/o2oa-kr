package com.x.file.assemble.control.jaxrs.file;

import com.x.base.core.project.exception.PromptException;

class ExceptionFileNotExisted extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionFileNotExisted(String id) {
		super("지정한 파일: {}이(가) 없습니다.", id);
	}
}
