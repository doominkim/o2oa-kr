package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAlreadyAddQueue extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionAlreadyAddQueue() {
		super("작업이 대기열에 추가되었습니다.");
	}

}
