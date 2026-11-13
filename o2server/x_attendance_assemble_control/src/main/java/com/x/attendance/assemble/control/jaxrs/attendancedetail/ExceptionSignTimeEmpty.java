package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.project.exception.PromptException;

class ExceptionSignTimeEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSignTimeEmpty() {
		super("직원 모바일 체크 정보의 체크 시간은 비워 둘 수 없습니다. 형식: HH:mm:ss." );
	}
}
