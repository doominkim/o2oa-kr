package com.x.attendance.assemble.control.factory;

import com.x.base.core.project.exception.PromptException;

class StatisticYearEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	StatisticYearEmptyException() {
		super("통계 연도는 비워 둘 수 없습니다.");
	}
}
