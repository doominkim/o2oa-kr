package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.project.exception.PromptException;

class ExceptionSingleDetailNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSingleDetailNotExists( ) {
		super("해당 인원과 체크 날짜의 직원 체크 정보가 없습니다!" );
	}
}
