package com.x.attendance.assemble.control.factory;

import com.x.base.core.project.exception.PromptException;

class EmployeeNamesEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	EmployeeNamesEmptyException() {
		super("직원 이름 목록은 비워 둘 수 없습니다.");
	}
}
