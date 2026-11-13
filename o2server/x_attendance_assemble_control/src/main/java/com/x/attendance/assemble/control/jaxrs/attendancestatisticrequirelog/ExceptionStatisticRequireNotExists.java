package com.x.attendance.assemble.control.jaxrs.attendancestatisticrequirelog;

import com.x.base.core.project.exception.PromptException;

class ExceptionStatisticRequireNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionStatisticRequireNotExists( String id ) {
		super("지정한 ID의 통계 요청 기록이 없습니다. ID:" + id );
	}
}
