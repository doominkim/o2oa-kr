package com.x.calendar.assemble.control.jaxrs.calendar;

import com.x.base.core.project.exception.PromptException;

class ExceptionCalendarInfoProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCalendarInfoProcess( Throwable e, String message ) {
		super("캘린더 정보를 처리하는 중 오류가 발생했습니다! message:" + message, e );
	}
	
	ExceptionCalendarInfoProcess( String message ) {
		super("캘린더 정보를 처리하는 중 오류가 발생했습니다! message:" + message );
	}
}
