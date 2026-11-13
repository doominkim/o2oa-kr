package com.x.calendar.assemble.control.jaxrs.event;

import com.x.base.core.project.exception.PromptException;

class ExceptionEventRRuleInvalid extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionEventRRuleInvalid( String rule ) {
		super("일정 반복 규칙이 올바르지 않습니다. RRule:" + rule);
	}
}
