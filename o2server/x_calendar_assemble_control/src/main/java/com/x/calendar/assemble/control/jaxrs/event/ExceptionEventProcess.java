package com.x.calendar.assemble.control.jaxrs.event;

import com.x.base.core.project.exception.PromptException;

class ExceptionEventProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionEventProcess( Throwable e, String message ) {
		super("일정 정보를 처리하는 중 오류가 발생했습니다! message:" + message, e );
	}
	
	ExceptionEventProcess( String message ) {
		super("일정 정보를 처리하는 중 오류가 발생했습니다! message:" + message );
	}
}
