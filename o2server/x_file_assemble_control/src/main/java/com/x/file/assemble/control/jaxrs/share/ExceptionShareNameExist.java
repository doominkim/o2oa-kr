package com.x.file.assemble.control.jaxrs.share;

import com.x.base.core.project.exception.PromptException;

class ExceptionShareNameExist extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionShareNameExist(String person, String name) {
		super("사용자: {}, 공유한 파일: {}이(가) 이미 있습니다.", person, name);
	}
}
