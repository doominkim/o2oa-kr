package com.x.bbs.assemble.control.jaxrs.configsetting.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionInsufficientPermissions extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionInsufficientPermissions( String name, String role ) {
		super("작업 권한이 없습니다. Name:" + name + ", Role:" + role );
	}
}
