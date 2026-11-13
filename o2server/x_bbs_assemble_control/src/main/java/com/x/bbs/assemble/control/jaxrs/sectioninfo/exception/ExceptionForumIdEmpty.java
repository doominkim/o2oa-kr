package com.x.bbs.assemble.control.jaxrs.sectioninfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionForumIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionForumIdEmpty() {
		super("포럼 분류 ID가 비어 있어 조회할 수 없습니다." );
	}
}
