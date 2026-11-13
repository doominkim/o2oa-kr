package com.x.meeting.assemble.control.jaxrs.meeting;

import com.x.base.core.project.exception.PromptException;

class ExceptionMeetingNotExist extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionMeetingNotExist(String flag) {
		super("회의 {}이(가) 없습니다.", flag);
	}
}
