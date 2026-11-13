package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionAppIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionAppIdEmpty() {
		super("분류 정보의 “소속 앱 게시판 ID”는 비워 둘 수 없습니다." );
	}
}
