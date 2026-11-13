package com.x.processplatform.service.processing.jaxrs.data;

import com.x.base.core.project.exception.PromptException;

class ExceptionModifyMerged extends PromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionModifyMerged(String workCompletedId) {
		super("완료된 문서가 이미 병합되었습니다, workCompleted: {}.", workCompletedId);
	}
}
