package com.x.attendance.assemble.control.jaxrs.attendancestatisticalcycle;

import com.x.base.core.project.exception.PromptException;

class ExceptionGetUnitNameByPerson extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionGetUnitNameByPerson( Throwable e, String name ) {
		super("개인 정보로 소속 조직 이름을 조회하는 중 오류가 발생했습니다! 인원:" + name, e );
	}
}
