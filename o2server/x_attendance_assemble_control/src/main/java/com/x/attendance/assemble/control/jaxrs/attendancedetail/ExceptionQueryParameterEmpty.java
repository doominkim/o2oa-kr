package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.project.exception.PromptException;

class ExceptionQueryParameterEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionQueryParameterEmpty() {
		super("사번, 직원 이름, 조회 날짜를 모두 비워 둘 수 없어 조회를 계속할 수 없습니다." );
	}
	public ExceptionQueryParameterEmpty(String message) {
		super(message );
	}
}
