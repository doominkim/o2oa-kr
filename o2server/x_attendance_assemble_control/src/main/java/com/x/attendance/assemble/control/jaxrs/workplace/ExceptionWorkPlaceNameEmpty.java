package com.x.attendance.assemble.control.jaxrs.workplace;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkPlaceNameEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkPlaceNameEmpty() {
		super("근무 장소 이름이 비어 있어 데이터를 저장할 수 없습니다.");
	}
}
