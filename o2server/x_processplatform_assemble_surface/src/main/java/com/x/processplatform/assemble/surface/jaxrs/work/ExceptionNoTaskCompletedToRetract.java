package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNoTaskCompletedToRetract extends LanguagePromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionNoTaskCompletedToRetract(String workId, String workLogId) {
		super("문서 {}을(를) 회수할 수 없습니다. 문서 로그 {}에 해당하는 처리자를 찾을 수 없습니다.", workId, workLogId);
	}

}
