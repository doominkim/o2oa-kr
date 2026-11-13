package com.x.bbs.assemble.control.jaxrs.roleinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionBindObjectTypeInvalid extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionBindObjectTypeInvalid( String type ) {
		super("대상 유형이 올바르지 않습니다. 사용자, 조직, 그룹 중 하나여야 합니다. Type:" + type );
	}
}
