package com.x.bbs.assemble.control.jaxrs.roleinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionBindUserNameEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionBindUserNameEmpty() {
		super("연결된 사용자 이름이 비어 있어 조회할 수 없습니다." );
	}
}
