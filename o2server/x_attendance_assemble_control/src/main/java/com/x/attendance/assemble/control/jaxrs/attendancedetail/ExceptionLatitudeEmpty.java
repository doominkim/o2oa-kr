package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.project.exception.PromptException;

class ExceptionLatitudeEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionLatitudeEmpty() {
		super("직원 모바일 체크 정보의 체크 위치 위도는 비워 둘 수 없습니다." );
	}
}
