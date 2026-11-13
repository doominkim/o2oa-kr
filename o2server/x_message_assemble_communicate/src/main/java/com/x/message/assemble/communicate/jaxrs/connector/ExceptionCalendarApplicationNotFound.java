package com.x.message.assemble.communicate.jaxrs.connector;

import com.x.base.core.project.exception.PromptException;

class ExceptionCalendarApplicationNotFound extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionCalendarApplicationNotFound() {
		super("x_calendar_assemble_control 앱을 찾을 수 없습니다.");
	}
}
