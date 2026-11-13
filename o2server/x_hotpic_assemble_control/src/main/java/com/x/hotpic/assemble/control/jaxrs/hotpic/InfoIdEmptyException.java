package com.x.hotpic.assemble.control.jaxrs.hotpic;

import com.x.base.core.project.exception.PromptException;

class InfoIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InfoIdEmptyException() {
		super("정보 ID가 비어 있어 데이터를 조회하거나 저장할 수 없습니다." );
	}
}
