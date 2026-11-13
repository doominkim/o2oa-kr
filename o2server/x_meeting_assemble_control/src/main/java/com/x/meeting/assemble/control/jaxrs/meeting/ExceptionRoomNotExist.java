package com.x.meeting.assemble.control.jaxrs.meeting;

import com.x.base.core.project.exception.PromptException;

class ExceptionRoomNotExist extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionRoomNotExist(String flag) {
		super("회의실 {}이(가) 없습니다.", flag);
	}
}
