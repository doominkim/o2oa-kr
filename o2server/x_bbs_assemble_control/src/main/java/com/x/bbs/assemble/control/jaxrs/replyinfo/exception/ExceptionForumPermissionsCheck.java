package com.x.bbs.assemble.control.jaxrs.replyinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionForumPermissionsCheck extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionForumPermissionsCheck( Throwable e, String name, String forum, String role ) {
		super("사용자 ["+name+"]에게 포럼 분류 ["+ forum +"]의 권한이 없습니다"+ role +"확인 중 오류가 발생했습니다!", e );
	}
}
