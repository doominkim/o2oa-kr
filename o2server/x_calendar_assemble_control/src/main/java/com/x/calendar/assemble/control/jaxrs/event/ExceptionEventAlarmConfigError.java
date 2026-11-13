package com.x.calendar.assemble.control.jaxrs.event;

import com.x.base.core.project.exception.PromptException;

class ExceptionEventAlarmConfigError extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionEventAlarmConfigError() {
		super("일정 알림 시간 설정이 올바르지 않습니다. 알림 설정을 확인하세요.");
	}
	
	ExceptionEventAlarmConfigError( String message ) {
		super("일정 알림 시간 설정이 올바르지 않습니다. " + message );
	}
}
