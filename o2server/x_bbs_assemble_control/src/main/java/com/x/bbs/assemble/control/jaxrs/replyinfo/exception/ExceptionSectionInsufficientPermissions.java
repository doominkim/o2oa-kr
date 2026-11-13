package com.x.bbs.assemble.control.jaxrs.replyinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSectionInsufficientPermissions extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSectionInsufficientPermissions( String name, String role ) {
		super("사용자에게 섹션 ["+ name +"]의 권한이 없습니다"+ role +"！" );
	}
}
