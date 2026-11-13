package com.x.bbs.assemble.control.jaxrs.foruminfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionForumNameEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionForumNameEmpty() {
		super("포럼 분류 이름 forumName이 비어 있어 조회할 수 없습니다." );
	}
}
