package com.x.hotpic.assemble.control.jaxrs.hotpic;

import com.x.base.core.project.exception.PromptException;

class InfoSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InfoSaveException( Throwable e  ) {
		super("인기 이미지 정보를 저장하는 중 오류가 발생했습니다." , e );
	}
}
