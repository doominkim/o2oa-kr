package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.project.exception.PromptException;

class ExceptionRecordDateEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionRecordDateEmpty() {
		super("직원 체크 정보의 체크 날짜는 비워 둘 수 없습니다. 형식: yyyy-mm-dd." );
	}
}
