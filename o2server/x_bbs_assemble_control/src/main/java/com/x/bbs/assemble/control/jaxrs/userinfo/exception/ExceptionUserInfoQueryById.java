package com.x.bbs.assemble.control.jaxrs.userinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionUserInfoQueryById extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionUserInfoQueryById( Throwable e, String id ) {
		super("지정한 ID의 BBS 사용자 정보를 조회하는 중 오류가 발생했습니다. ID:" + id, e );
	}
}
