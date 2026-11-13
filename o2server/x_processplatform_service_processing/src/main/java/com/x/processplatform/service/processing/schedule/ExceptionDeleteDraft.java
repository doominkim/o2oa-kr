package com.x.processplatform.service.processing.schedule;

import com.x.base.core.project.exception.PromptException;

class ExceptionDeleteDraft extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionDeleteDraft(Exception e, String id, String title, String sequence) {
		super(e, "정체된 임시저장 문서 삭제 실패, id: {}, title: {}, sequence: {}.");
	}

}
