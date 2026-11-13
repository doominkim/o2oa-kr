package com.x.processplatform.service.processing.jaxrs.workcompleted;

import com.x.base.core.project.exception.PromptException;

class ExceptionSplittingNotRollback extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionSplittingNotRollback(String workId, String workLogId) {
		super("문서를 분할 중인 상태로 롤백할 수 없습니다, work: {}, workLog: {}.", workId, workLogId);
	}
}
