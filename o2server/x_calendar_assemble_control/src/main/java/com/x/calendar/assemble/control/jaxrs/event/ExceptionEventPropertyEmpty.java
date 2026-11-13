package com.x.calendar.assemble.control.jaxrs.event;

import com.x.base.core.project.exception.PromptException;

class ExceptionEventPropertyEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionEventPropertyEmpty( String title ) {
		super("일정 ["+ title +"] 속성이 비어 있어 데이터를 처리할 수 없습니다.");
	}
}
