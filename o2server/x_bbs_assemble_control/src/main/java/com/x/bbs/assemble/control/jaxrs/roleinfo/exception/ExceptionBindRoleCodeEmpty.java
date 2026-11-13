package com.x.bbs.assemble.control.jaxrs.roleinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionBindRoleCodeEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionBindRoleCodeEmpty() {
		super("연결된 역할 코드가 비어 있어 조회할 수 없습니다." );
	}
}
