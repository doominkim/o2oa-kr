package com.x.processplatform.assemble.surface;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionRecordProcessing extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionRecordProcessing(String id) {
		super("결재 기록 처리 실패: {}.", id);
	}

}
