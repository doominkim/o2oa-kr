package com.x.attendance.assemble.control.factory;

import com.x.base.core.project.exception.PromptException;

class RecordDateEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	RecordDateEmptyException() {
		super("체크 날짜는 비워 둘 수 없습니다.");
	}
}
