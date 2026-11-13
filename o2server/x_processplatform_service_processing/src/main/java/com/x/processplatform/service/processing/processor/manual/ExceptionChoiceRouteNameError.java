package com.x.processplatform.service.processing.processor.manual;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.RunningException;

class ExceptionChoiceRouteNameError extends RunningException {

	private static final long serialVersionUID = 9085364457175859374L;

	ExceptionChoiceRouteNameError(List<String> taskCompletedIds) {
		super("처리 선택 오류, 결재 완료 목록: {}.", StringUtils.join(taskCompletedIds, ","));
	}

}
