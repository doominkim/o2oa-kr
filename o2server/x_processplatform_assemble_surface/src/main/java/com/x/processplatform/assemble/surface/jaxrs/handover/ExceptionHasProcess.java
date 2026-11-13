package com.x.processplatform.assemble.surface.jaxrs.handover;

import com.x.base.core.project.exception.PromptException;

class ExceptionHasProcess extends PromptException {

	private static final long serialVersionUID = -6309924494820409666L;

	ExceptionHasProcess(String operate) {
		super("이 작업은 실행 중이거나 이미 완료되었습니다, {}.", operate);
	}
}
