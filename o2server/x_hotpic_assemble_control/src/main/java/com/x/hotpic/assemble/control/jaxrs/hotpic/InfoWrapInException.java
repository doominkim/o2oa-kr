package com.x.hotpic.assemble.control.jaxrs.hotpic;

import com.x.base.core.project.exception.PromptException;

class InfoWrapInException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InfoWrapInException( Throwable e  ) {
		super("입력 데이터를 인기 정보 객체로 변환하는 중 오류가 발생했습니다." , e );
	}
}
