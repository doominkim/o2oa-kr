package com.x.portal.assemble.designer.jaxrs.templatepage;

import com.x.base.core.project.exception.LanguagePromptException;

class TemplatePageNotExistedException extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	TemplatePageNotExistedException(String id) {
		super("지정한 페이지가 없습니다: {}.", id);
	}
}
