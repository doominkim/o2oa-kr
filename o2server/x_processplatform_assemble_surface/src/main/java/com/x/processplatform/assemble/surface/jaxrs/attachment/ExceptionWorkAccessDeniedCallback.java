package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.exception.CallbackPromptException;

class ExceptionWorkAccessDeniedCallback extends CallbackPromptException {

	private static final long serialVersionUID = 9085364457175859374L;

	ExceptionWorkAccessDeniedCallback(String callbackName, String person, String title, String workId) {
		super(callbackName, "사용자: {}에게 문서 title: {} id: {}에 접근할 권한이 없습니다.", person, title, workId);
	}

}
