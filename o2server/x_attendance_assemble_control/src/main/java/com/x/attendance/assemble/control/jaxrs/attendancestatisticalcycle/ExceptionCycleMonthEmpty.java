package com.x.attendance.assemble.control.jaxrs.attendancestatisticalcycle;

import com.x.base.core.project.exception.PromptException;

class ExceptionCycleMonthEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionCycleMonthEmpty() {
		super("조회 매개변수인 기간 월 month가 없어 데이터를 조회할 수 없습니다");
	}
}
