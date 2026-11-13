package com.x.attendance.assemble.control.jaxrs.attendancestatisticalcycle;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonHasNoIdentity extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionPersonHasNoIdentity() {
		super("사용자에게 할당된 소속이 없습니다. 사용자의 조직 정보를 확인하십시오.");
	}
	
	public ExceptionPersonHasNoIdentity(String name ) {
		super("사용자 '"+ name +"'에게 할당된 소속이 없습니다. 사용자의 조직 정보를 확인하십시오.");
	}
}