package com.x.attendance.assemble.control.jaxrs.attendanceworkdayconfig;

import com.x.base.core.project.exception.PromptException;

class ExceptionWorkDayConfigNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkDayConfigNotExists( String id ) {
		super("지정한 ID의 휴일·근무일 설정이 없습니다. ID:" + id );
	}
}
