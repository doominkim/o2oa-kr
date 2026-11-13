package com.x.bbs.assemble.control.jaxrs.permissioninfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionRoleCodeEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionRoleCodeEmpty() {
		super("역할 코드 roleCode가 비어 있어 조회할 수 없습니다." );
	}
}
