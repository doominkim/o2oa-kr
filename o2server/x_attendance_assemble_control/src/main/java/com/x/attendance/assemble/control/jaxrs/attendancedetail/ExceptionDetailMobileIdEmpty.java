package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.project.exception.PromptException;

class ExceptionDetailMobileIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionDetailMobileIdEmpty() {
		super("직원 모바일 체크 기록 ID가 비어 있어 데이터를 조회할 수 없습니다." );
	}
}
