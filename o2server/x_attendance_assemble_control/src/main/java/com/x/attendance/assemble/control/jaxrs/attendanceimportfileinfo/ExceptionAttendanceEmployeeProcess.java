package com.x.attendance.assemble.control.jaxrs.attendanceimportfileinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttendanceEmployeeProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionAttendanceEmployeeProcess( Throwable e, String message ) {
		super("근태 인원 설정 처리 중 오류가 발생했습니다! message:" + message, e );
	}
}
