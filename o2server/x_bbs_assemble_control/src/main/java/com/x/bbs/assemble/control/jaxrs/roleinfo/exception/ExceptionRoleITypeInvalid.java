package com.x.bbs.assemble.control.jaxrs.roleinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionRoleITypeInvalid extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionRoleITypeInvalid( String type ) {
		super("역할 유형이 올바르지 않아 역할 정보를 저장할 수 없습니다. Type:" + type );
	}
}
