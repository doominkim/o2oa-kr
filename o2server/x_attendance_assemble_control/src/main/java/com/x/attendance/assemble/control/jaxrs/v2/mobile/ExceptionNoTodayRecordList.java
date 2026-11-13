package com.x.attendance.assemble.control.jaxrs.v2.mobile;

import com.x.base.core.project.exception.PromptException;

public class ExceptionNoTodayRecordList extends PromptException {

 

	public ExceptionNoTodayRecordList() {
		super(  "체크 기록 목록 정보를 찾을 수 없습니다.");
	}
}
