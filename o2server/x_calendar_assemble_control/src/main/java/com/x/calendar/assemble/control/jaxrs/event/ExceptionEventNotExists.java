package com.x.calendar.assemble.control.jaxrs.event;

import com.x.base.core.project.exception.PromptException;

class ExceptionEventNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionEventNotExists( String id ) {
		super("지정한 일정 정보가 없습니다. ID:" + id );
	}
}
