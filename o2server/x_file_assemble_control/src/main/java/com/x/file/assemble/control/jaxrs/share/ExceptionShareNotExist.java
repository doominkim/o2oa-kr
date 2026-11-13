package com.x.file.assemble.control.jaxrs.share;

import com.x.base.core.project.exception.PromptException;

class ExceptionShareNotExist extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionShareNotExist(String id) {
		super("지정한 공유 파일: {}이(가) 없습니다.", id);
	}
}
