package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import com.x.base.core.project.exception.PromptException;

class ExceptionCanNotFindTopUnitWithUnitName extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionCanNotFindTopUnitWithUnitName( String name ) {
		super("조직 정보로 상위 최상위 조직 이름을 찾을 수 없습니다. 조직:" + name );
	}
}
