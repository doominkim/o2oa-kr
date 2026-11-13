package com.x.attendance.assemble.control.jaxrs.attendancestatisticalcycle;

import com.x.base.core.project.exception.PromptException;

class ExceptionStatisticCycleNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionStatisticCycleNotExists( String id ) {
		super("지정한 ID의 통계 기간이 없습니다. ID:" + id );
	}
}
