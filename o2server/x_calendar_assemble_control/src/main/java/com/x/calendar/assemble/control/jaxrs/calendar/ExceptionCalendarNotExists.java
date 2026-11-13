package com.x.calendar.assemble.control.jaxrs.calendar;

import com.x.base.core.project.exception.PromptException;

class ExceptionCalendarNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCalendarNotExists( String id ) {
		super("지정한 캘린더 정보가 없습니다. ID:" + id );
	}
}
