package com.x.meeting.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.http.EffectivePerson;

class ExceptionMeetingAccessDenied extends PromptException {

	private static final long serialVersionUID = 7237855733312562652L;

	ExceptionMeetingAccessDenied(EffectivePerson effectivePerson, String name) {
		super("사용자: {}에게 회의: {}에 접근할 권한이 없습니다..", effectivePerson.getName(), name);

	}
}
