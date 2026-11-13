package com.x.attendance.assemble.control.jaxrs.workplace;

import com.x.base.core.project.exception.PromptException;

class ExceptionWorkPlaceIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkPlaceIdEmpty() {
		super("근무 장소 ID가 비어 있어 데이터를 조회할 수 없습니다.");
	}
}
