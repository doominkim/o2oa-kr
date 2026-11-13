package com.x.cms.assemble.control.jaxrs.image;

import com.x.base.core.project.exception.PromptException;

class ExceptionURLEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionURLEmpty() {
		super("URL이 비어 있어 조회할 수 없습니다." );
	}
}
