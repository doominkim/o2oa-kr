package com.x.bbs.assemble.control.jaxrs.userinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionUserInfoNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionUserInfoNotExists( String id ) {
		super("지정한 ID의 BBS 사용자 정보가 없습니다. ID:" + id );
	}
}
