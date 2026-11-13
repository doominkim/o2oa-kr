package com.x.attendance.assemble.control.jaxrs.attendanceimportfileinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionFileIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionFileIdEmpty() {
		super("조회 매개변수 ID가 비어 있어 조회할 수 없습니다.");
	}
}
