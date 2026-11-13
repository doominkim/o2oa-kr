package com.x.attendance.assemble.control.jaxrs.attendanceadmin;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttendanceAdminProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionAttendanceAdminProcess(Throwable e, String message ) {
		super("근태 관리자 정보 설정 처리 중 오류가 발생했습니다! message:" + message, e );
	}
}
