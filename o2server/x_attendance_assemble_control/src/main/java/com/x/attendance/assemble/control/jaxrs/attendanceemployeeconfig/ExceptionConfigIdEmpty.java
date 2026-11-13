package com.x.attendance.assemble.control.jaxrs.attendanceemployeeconfig;

import com.x.base.core.project.exception.PromptException;

class ExceptionConfigIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionConfigIdEmpty() {
		super("조회 매개변수 ID가 비어 있어 조회할 수 없습니다.");
	}
}
