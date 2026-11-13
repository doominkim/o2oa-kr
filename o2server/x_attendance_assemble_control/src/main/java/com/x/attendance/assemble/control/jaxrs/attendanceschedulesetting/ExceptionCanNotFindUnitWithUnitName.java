package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import com.x.base.core.project.exception.PromptException;

class ExceptionCanNotFindUnitWithUnitName extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionCanNotFindUnitWithUnitName( String name ) {
		super("조직 이름으로 조직 정보를 찾을 수 없습니다. UnitName:" + name );
	}
}
