package com.x.processplatform.assemble.surface.jaxrs.handover;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonInvalid extends PromptException {

	private static final long serialVersionUID = 8077614120025290009L;

	ExceptionPersonInvalid() {
		super("사용자는 전체 이름이어야 합니다. 예: 홍길동@hong@P.");
	}
}
