package com.x.calendar.assemble.control.jaxrs.calendar;

import com.x.base.core.project.exception.PromptException;

class ExceptionCalendarNameEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCalendarNameEmpty() {
		super("캘린더 이름 name이 비어 있어 조회하거나 저장할 수 없습니다.");
	}
}
