package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import com.x.base.core.project.exception.PromptException;

class ExceptionScheduleUnitEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionScheduleUnitEmpty() {
		super("조회 매개변수 unitName이 비어 있어 조회할 수 없습니다.");
	}
}
