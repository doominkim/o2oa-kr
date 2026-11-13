package com.x.bbs.assemble.control.jaxrs.foruminfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionForumTypeCategoryInvalid extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionForumTypeCategoryInvalid( String type ) {
		super("전달된 [주제 카테고리]가 올바르지 않아 데이터를 저장할 수 없습니다! 카테고리:" + type );
	}
}
