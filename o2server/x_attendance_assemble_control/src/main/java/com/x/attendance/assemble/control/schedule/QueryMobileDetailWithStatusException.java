package com.x.attendance.assemble.control.schedule;

import com.x.base.core.project.exception.PromptException;

class QueryMobileDetailWithStatusException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	QueryMobileDetailWithStatusException( int stauts ) {
		super("상태별 모바일 체크 기록 조회 중 오류가 발생했습니다. 상태:" + stauts );
	}
}
