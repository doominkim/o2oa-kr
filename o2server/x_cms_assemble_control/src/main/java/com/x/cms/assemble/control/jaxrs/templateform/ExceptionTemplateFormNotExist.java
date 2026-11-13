package com.x.cms.assemble.control.jaxrs.templateform;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionTemplateFormNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionTemplateFormNotExist(String flag) {
		super("지정한 양식이 없습니다: {}", flag);
	}
}
