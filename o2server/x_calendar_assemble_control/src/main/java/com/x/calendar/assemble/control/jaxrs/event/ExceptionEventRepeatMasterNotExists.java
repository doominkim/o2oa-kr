package com.x.calendar.assemble.control.jaxrs.event;

import com.x.base.core.project.exception.PromptException;

class ExceptionEventRepeatMasterNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionEventRepeatMasterNotExists( String masterId ) {
		super("지정한 반복 일정의 기준 정보가 없습니다. MASTERID:" + masterId );
	}
}
