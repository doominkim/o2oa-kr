package com.x.bbs.assemble.control.jaxrs.replyinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionForumInsufficientPermission extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionForumInsufficientPermission( String forum, String role ) {
		super("사용자에게 포럼 분류 ["+ forum +"]의 권한이 없습니다"+ role +"！" );
	}
}
