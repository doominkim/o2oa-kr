package com.x.calendar.assemble.control.jaxrs.setting;

import com.x.base.core.project.exception.PromptException;

class ExceptionSettingIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionSettingIdEmpty() {
		super("조회 매개변수 Id가 비어 있어 조회할 수 없습니다.");
	}
}
