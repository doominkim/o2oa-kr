package com.x.attendance.assemble.control.jaxrs.attendancestatisticalcycle;

import com.x.base.core.project.exception.PromptException;

class ExceptionCanNotFindUnitNameByPerson extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionCanNotFindUnitNameByPerson( String name ) {
		super("개인 정보로 소속 조직 이름을 찾을 수 없습니다. 인원의 소속 조직 설정을 확인하십시오! 인원:" + name );
	}
}
