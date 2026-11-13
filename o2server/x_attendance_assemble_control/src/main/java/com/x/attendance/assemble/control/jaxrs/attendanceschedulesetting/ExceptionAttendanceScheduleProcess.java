package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttendanceScheduleProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionAttendanceScheduleProcess( Throwable e, String message ) {
		super("근태 시간 설정 처리 중 오류가 발생했습니다! message:" + message, e );
	}
}
