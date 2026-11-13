package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import com.x.base.core.project.exception.PromptException;

class ExceptionQueryStatisticUnitNameEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionQueryStatisticUnitNameEmpty() {
		super("조회 매개변수인 조직 이름 name이 없어 데이터를 조회할 수 없습니다");
	}
}
