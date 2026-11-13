package com.x.calendar.assemble.control.jaxrs.calendar;

import com.x.base.core.project.exception.PromptException;

class ExceptionCalendarIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCalendarIdEmpty() {
		super("캘린더 ID 매개변수가 비어 있어 조회할 수 없습니다.");
	}
}
