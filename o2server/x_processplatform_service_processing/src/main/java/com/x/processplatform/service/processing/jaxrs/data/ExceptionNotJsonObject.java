package com.x.processplatform.service.processing.jaxrs.data;

import com.x.base.core.project.exception.PromptException;

class ExceptionNotJsonObject extends PromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionNotJsonObject( ) {
		super("수정할 데이터는 배열일 수 없습니다.");
	}
}
