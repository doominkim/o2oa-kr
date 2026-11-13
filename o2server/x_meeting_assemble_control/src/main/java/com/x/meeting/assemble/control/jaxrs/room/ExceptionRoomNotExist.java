package com.x.meeting.assemble.control.jaxrs.room;

import com.x.base.core.project.exception.PromptException;

class ExceptionRoomNotExist extends PromptException {

	private static final long serialVersionUID = 7237855733312562652L;

	ExceptionRoomNotExist(String id) {
		super("지정한 회의실을 찾을 수 없습니다: {}.", id);
	}
}
