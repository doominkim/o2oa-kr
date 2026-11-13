package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.project.exception.PromptException;

class ExceptionDetaillMobileNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionDetaillMobileNotExists( String id ) {
		super("직원 모바일 체크 정보가 없습니다! ID:" + id );
	}
}
