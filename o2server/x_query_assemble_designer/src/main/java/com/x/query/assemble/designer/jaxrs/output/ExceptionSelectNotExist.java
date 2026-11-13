package com.x.query.assemble.designer.jaxrs.output;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionSelectNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionSelectNotExist(String flag) {
		super("데이터 세트: {}이(가) 없습니다.", flag);
	}
}
