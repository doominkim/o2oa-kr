package com.x.calendar.assemble.control.jaxrs.calendar;

import com.x.base.core.project.exception.PromptException;

class ExceptionCalendarTargetInvalid extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCalendarTargetInvalid( String name ) {
		super("캘린더 대상 식별자 '"+name+"'이(가) 올바르지 않습니다. 사용자 식별자 또는 조직 식별자여야 합니다.");
	}
}
