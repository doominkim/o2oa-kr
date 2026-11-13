package com.x.portal.assemble.designer.jaxrs.file;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyName extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionEmptyName() {
		super("이름은 비워 둘 수 없습니다.");
	}
}
