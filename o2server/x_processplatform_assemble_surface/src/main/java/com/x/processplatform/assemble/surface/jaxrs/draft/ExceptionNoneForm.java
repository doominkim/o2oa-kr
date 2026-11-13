package com.x.processplatform.assemble.surface.jaxrs.draft;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNoneForm extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionNoneForm() {
		super("양식을 찾을 수 없습니다.");
	}
}
