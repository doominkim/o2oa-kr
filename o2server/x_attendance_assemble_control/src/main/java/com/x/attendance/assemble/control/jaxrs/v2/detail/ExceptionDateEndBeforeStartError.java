package com.x.attendance.assemble.control.jaxrs.v2.detail;

import com.x.base.core.project.exception.PromptException;

public class ExceptionDateEndBeforeStartError extends PromptException {


	private static final long serialVersionUID = 5464536178018732958L;

	public ExceptionDateEndBeforeStartError() {
		super(  "시작 날짜는 종료 날짜보다 늦을 수 없습니다.");
	}
}
