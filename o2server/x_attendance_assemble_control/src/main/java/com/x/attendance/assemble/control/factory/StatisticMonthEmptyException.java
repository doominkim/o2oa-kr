package com.x.attendance.assemble.control.factory;

import com.x.base.core.project.exception.PromptException;

class StatisticMonthEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	StatisticMonthEmptyException() {
		super("통계 월은 비워 둘 수 없습니다.");
	}
}
