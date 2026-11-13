package com.x.calendar.assemble.control.jaxrs.setting;

import com.x.base.core.project.exception.PromptException;

class ExceptionSettingInfoProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionSettingInfoProcess( Throwable e, String message ) {
		super("설정 정보를 처리하는 중 오류가 발생했습니다! message:" + message, e );
	}
}
