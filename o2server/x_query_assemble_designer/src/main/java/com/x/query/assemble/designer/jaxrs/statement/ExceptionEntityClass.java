package com.x.query.assemble.designer.jaxrs.statement;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEntityClass extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionEntityClass(String className) {
		super("지정한 유형을 가져올 수 없습니다: {}.", className);
	}
}
