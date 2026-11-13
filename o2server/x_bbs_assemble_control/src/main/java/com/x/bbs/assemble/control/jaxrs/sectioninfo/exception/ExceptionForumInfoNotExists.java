package com.x.bbs.assemble.control.jaxrs.sectioninfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionForumInfoNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionForumInfoNotExists( String id ) {
		super("지정한 ID의 포럼 분류가 없습니다. ID:" + id );
	}
}
