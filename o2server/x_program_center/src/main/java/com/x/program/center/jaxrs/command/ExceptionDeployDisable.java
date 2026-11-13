package com.x.program.center.jaxrs.command;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDeployDisable extends LanguagePromptException {

	private static final long serialVersionUID = -5285650034988505084L;

	public ExceptionDeployDisable() {
		super("war 패키지 배포가 비활성화되었습니다.");
	}

}
