package com.x.bbs.assemble.control.jaxrs.userinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionUserInfoWrapOut extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionUserInfoWrapOut( Throwable e ) {
		super("조회 결과를 출력용 데이터로 변환하는 중 오류가 발생했습니다.", e );
	}
}
