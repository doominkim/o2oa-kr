package com.x.calendar.assemble.control.jaxrs.event;

import com.x.base.core.project.exception.PromptException;

class ExceptionEventIsNotRecurrenceEvent extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionEventIsNotRecurrenceEvent( String id ) {
		super("반복 일정이 아닙니다. ID:" + id);
	}
}
