package com.x.calendar.assemble.control.jaxrs.setting;

import com.x.base.core.project.exception.PromptException;

class ExceptionSettingNameEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionSettingNameEmpty() {
		super("설정 이름 Name이 비어 있어 조회하거나 저장할 수 없습니다.");
	}
}
