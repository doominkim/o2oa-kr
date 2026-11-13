package com.x.attendance.assemble.control.jaxrs.selfholiday;

import com.x.base.core.project.exception.PromptException;

class ExceptionSelfHolidayProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSelfHolidayProcess( Throwable e, String message ) {
		super("근태 통계 처리 중 오류가 발생했습니다! message:" + message, e );
	}
	public ExceptionSelfHolidayProcess( String message ) {
		super( message );
	}
}
