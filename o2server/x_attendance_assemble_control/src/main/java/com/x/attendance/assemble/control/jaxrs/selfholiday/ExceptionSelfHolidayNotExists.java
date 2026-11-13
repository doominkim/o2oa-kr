package com.x.attendance.assemble.control.jaxrs.selfholiday;

import com.x.base.core.project.exception.PromptException;

class ExceptionSelfHolidayNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSelfHolidayNotExists( String id ) {
		super("지정한 ID의 직원 휴가 신청 정보가 없습니다. ID:" + id );
	}
}
