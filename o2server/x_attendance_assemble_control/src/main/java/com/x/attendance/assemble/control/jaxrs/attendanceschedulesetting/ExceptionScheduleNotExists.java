package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import com.x.base.core.project.exception.PromptException;

class ExceptionScheduleNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionScheduleNotExists( String id ) {
		super("지정한 조직 근무 편성 정보가 없습니다. ID:" + id );
	}
}
