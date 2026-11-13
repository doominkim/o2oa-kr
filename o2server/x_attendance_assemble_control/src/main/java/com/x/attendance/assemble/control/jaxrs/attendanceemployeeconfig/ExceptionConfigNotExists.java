package com.x.attendance.assemble.control.jaxrs.attendanceemployeeconfig;

import com.x.base.core.project.exception.PromptException;

class ExceptionConfigNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionConfigNotExists( String id ) {
		super("지정한 인원 근태 설정 데이터가 없습니다. ID:" + id );
	}
}
