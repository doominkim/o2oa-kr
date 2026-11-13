package com.x.program.center.jaxrs.module;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDeployDisable extends LanguagePromptException {

	private static final long serialVersionUID = -5285650034988505084L;

	public ExceptionDeployDisable() {
		super("리소스 배포가 비활성화되었습니다.");
	}

}
