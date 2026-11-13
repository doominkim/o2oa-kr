package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonHasNoIdenitity extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionPersonHasNoIdenitity( String name ) {
		super("직원에게 소속 정보가 설정되지 않았습니다. 직원의 조직이 정상인지 확인하십시오! name:" + name );
	}
}
