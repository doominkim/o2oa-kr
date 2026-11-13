package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonHasNoUnit extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionPersonHasNoUnit( String name ) {
		super( "직원 이름으로 조직 정보를 찾을 수 없습니다! name:" + name );
	}
}
