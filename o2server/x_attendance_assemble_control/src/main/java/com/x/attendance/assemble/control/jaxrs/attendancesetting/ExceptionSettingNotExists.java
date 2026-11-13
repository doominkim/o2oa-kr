package com.x.attendance.assemble.control.jaxrs.attendancesetting;

import com.x.base.core.project.exception.PromptException;

class ExceptionSettingNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSettingNotExists( String id ) {
		super("지정한 근태 시스템 설정이 없습니다. ID:" + id );
	}
}
