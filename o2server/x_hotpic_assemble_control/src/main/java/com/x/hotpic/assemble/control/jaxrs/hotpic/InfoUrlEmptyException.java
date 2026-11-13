package com.x.hotpic.assemble.control.jaxrs.hotpic;

import com.x.base.core.project.exception.PromptException;

class InfoUrlEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InfoUrlEmptyException() {
		super("정보 접근 주소가 비어 있어 데이터를 조회하거나 저장할 수 없습니다." );
	}
}
