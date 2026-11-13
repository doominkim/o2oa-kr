package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionBeforeV82TaskUnsupportedAdd extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionBeforeV82TaskUnsupportedAdd(String id) {
		super("현재 결재 대기는 결재자 추가를 지원하지 않습니다. 관리자에게 문의하세요.: {}.", id);
	}

}
