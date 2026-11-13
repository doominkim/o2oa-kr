package com.x.calendar.assemble.control.jaxrs.calendar;

import com.x.base.core.project.exception.PromptException;

class ExceptionCalendarPropertyEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCalendarPropertyEmpty( String name ) {
		super("캘린더 속성 '"+name+"'이(가) 비어 있어 저장할 수 없습니다.");
	}
}
