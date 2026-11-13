package com.x.attendance.assemble.control.jaxrs.v2.appeal;

import com.x.base.core.project.exception.PromptException;

public class ExceptionPersonNotEqual extends PromptException {


	private static final long serialVersionUID = 7385829212190071815L;

	public ExceptionPersonNotEqual() {
		super(  "현재 작업자와 데이터 소유 사용자가 일치하지 않습니다.");
	}
}
