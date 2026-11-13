package com.x.attendance.assemble.control.jaxrs.v2.detail;

import com.x.base.core.project.exception.PromptException;

public class ExceptionDateError extends PromptException {


	private static final long serialVersionUID = 5464536178018732958L;

	public ExceptionDateError() {
		super(  "날짜가 올바르지 않습니다. 과거 날짜를 입력하십시오. 형식: yyyy-MM-dd.");
	}
}
