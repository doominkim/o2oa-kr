package com.x.processplatform.service.processing.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptySplitValue extends PromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionEmptySplitValue(String id) {
		super("문서: {}, 분할 값은 비워 둘 수 없습니다.", id);
	}
}
