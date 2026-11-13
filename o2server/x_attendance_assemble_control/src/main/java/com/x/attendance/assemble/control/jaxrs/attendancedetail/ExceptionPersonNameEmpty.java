package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonNameEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionPersonNameEmpty() {
		super("체크 정보의 직원 이름은 비워 둘 수 없습니다." );
	}
}
