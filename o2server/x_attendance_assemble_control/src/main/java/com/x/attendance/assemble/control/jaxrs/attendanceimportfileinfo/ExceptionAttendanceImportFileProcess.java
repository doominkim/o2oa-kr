package com.x.attendance.assemble.control.jaxrs.attendanceimportfileinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttendanceImportFileProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionAttendanceImportFileProcess( Throwable e, String message ) {
		super("가져오기 파일 정보 처리 중 오류가 발생했습니다! message:" + message, e );
	}
}
