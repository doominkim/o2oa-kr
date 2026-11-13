package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionFileNotExist extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionFileNotExist(String id) {
		super("지정한 파일: {}이(가) 없습니다.", id);
	}
	ExceptionFileNotExist(String id, String fileId) {
		super("지정한 파일: {}의 첨부파일 {}이(가) 없습니다.", id, fileId);
	}
}
