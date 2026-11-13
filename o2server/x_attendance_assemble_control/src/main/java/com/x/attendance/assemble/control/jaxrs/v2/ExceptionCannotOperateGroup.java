package com.x.attendance.assemble.control.jaxrs.v2;

import com.x.base.core.project.exception.PromptException;

public class ExceptionCannotOperateGroup extends PromptException {

 

	private static final long serialVersionUID = 2816307410375246495L;

	public ExceptionCannotOperateGroup(String name) {
		super(name + " 근태 그룹이 저장되지 않아 작업할 수 없습니다.");
	}
}
