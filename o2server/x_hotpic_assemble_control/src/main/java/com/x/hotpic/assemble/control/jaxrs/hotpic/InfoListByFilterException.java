package com.x.hotpic.assemble.control.jaxrs.hotpic;

import com.x.base.core.project.exception.PromptException;

class InfoListByFilterException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InfoListByFilterException( Throwable e ) {
		super("필터 조건으로 정보 객체 목록을 조회하는 중 오류가 발생했습니다.", e );
	}
}
