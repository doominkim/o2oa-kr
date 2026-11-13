package com.x.calendar.assemble.control.jaxrs.event;

import com.x.base.core.project.exception.PromptException;

class ExceptionTaskEventCanNotRepeatMaster extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionTaskEventCanNotRepeatMaster( String id ) {
		super("할 일 일정은 반복할 수 없습니다. 반복 규칙을 설정하지 마세요. REPEATMASTERID:" + id);
	}
}
