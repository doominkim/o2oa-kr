package com.x.program.center.jaxrs.deploy;

import com.x.base.core.project.exception.PromptException;

class ExceptionDeployFail extends PromptException {

	private static final long serialVersionUID = -5285650034988505084L;

	public ExceptionDeployFail() {
		super("배포에 실패했습니다.");
	}

}
