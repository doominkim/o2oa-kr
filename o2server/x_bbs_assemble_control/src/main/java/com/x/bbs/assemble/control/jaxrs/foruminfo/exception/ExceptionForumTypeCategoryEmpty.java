package com.x.bbs.assemble.control.jaxrs.foruminfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionForumTypeCategoryEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionForumTypeCategoryEmpty() {
		super("포럼 분류의 주제 카테고리 typeCategory가 비어 있어 조회 또는 저장할 수 없습니다." );
	}
}
