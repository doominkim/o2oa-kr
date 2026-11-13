package com.x.attendance.assemble.control.factory;

import com.x.base.core.project.exception.PromptException;

class TopUnitNamesEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TopUnitNamesEmptyException() {
		super("최상위 조직 이름 목록은 비워 둘 수 없습니다.");
	}
}
