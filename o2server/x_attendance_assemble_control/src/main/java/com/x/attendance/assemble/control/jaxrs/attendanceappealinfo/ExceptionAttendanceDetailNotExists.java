package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttendanceDetailNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionAttendanceDetailNotExists( String id ) {
		super("직원 체크 정보가 없습니다! ID:" + id );
	}
}
