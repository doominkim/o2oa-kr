package com.x.bbs.assemble.control.jaxrs.userinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionUserInfoQueryByName extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionUserInfoQueryByName( Throwable e, String name ) {
		super("지정한 이름의 BBS 사용자 정보를 조회하는 중 오류가 발생했습니다. Name:" + name, e );
	}
}
