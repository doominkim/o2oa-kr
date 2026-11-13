package com.x.query.assemble.designer.jaxrs.stat;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNameExist extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionNameExist(String str) {
		super("이름: {}, 이미 있습니다.", str);
	}
}
