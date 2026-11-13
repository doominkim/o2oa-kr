package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.project.exception.PromptException;

class ExceptionFileIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionFileIdEmpty() {
		super("직원 체크 기록 가져오기 파일 ID가 비어 있어 데이터를 조회할 수 없습니다." );
	}
}
