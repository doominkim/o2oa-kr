package com.x.bbs.assemble.control.jaxrs.roleinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionRoleITypeEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionRoleITypeEmpty() {
		super("역할 유형이 비어 있어 역할 정보를 저장할 수 없습니다" );
	}
}
