package com.x.attendance.assemble.control.jaxrs.attendancesetting;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttendanceSettingProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionAttendanceSettingProcess( Throwable e, String message ) {
		super("시스템 설정 처리 중 오류가 발생했습니다! message:" + message, e );
	}
}
