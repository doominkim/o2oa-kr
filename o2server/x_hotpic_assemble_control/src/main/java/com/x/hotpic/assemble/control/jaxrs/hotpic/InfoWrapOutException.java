package com.x.hotpic.assemble.control.jaxrs.hotpic;

import com.x.base.core.project.exception.PromptException;

class InfoWrapOutException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InfoWrapOutException( Throwable e  ) {
		super("조회 결과를 출력용 데이터로 변환하는 중 오류가 발생했습니다." , e );
	}
}
