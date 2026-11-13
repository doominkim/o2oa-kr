package com.x.hotpic.assemble.control.jaxrs.hotpic;

import com.x.base.core.project.exception.PromptException;

class InfoQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InfoQueryByIdException( Throwable e, String id ) {
		super("정보 ID로 정보 객체를 조회하는 중 오류가 발생했습니다. ID:" + id, e );
	}
}
