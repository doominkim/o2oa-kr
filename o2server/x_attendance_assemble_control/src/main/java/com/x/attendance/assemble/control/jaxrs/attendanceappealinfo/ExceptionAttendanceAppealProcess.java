package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttendanceAppealProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionAttendanceAppealProcess( Throwable e, String message ) {
		super("근태 결과 이의 신청 정보 처리 중 오류가 발생했습니다! message:" + message, e );
	}
	
	public ExceptionAttendanceAppealProcess( String message ) {
		super( message );
	}
}
