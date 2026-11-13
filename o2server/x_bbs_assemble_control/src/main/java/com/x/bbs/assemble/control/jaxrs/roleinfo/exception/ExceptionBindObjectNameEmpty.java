package com.x.bbs.assemble.control.jaxrs.roleinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionBindObjectNameEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionBindObjectNameEmpty() {
		super("연결 대상이 비어 있어 조회할 수 없습니다." );
	}
}
