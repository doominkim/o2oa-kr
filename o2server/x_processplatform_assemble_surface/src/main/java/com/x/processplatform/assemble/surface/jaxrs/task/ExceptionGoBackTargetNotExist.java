package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionGoBackTargetNotExist extends LanguagePromptException {

	private static final long serialVersionUID = 3674258561301135294L;

	ExceptionGoBackTargetNotExist(String activity) {
		super("반송 대상 단계가 없습니다, activity: {}.", activity);
	}

}