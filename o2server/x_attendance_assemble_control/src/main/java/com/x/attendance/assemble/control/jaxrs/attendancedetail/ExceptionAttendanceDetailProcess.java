package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttendanceDetailProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionAttendanceDetailProcess( Throwable e, String message ) {
		super("근태 체크 데이터 처리 중 오류가 발생했습니다! message:" + message, e );
	}
	
	public ExceptionAttendanceDetailProcess( String message ) {
		super("근태 체크 데이터 처리 중 오류가 발생했습니다! message:" + message );
	}
}
