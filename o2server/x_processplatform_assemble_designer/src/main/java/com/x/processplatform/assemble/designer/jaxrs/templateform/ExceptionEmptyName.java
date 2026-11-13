package com.x.processplatform.assemble.designer.jaxrs.templateform;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyName extends PromptException {

	private static final long serialVersionUID = 6984800093761853101L;

	ExceptionEmptyName() {
		super("이름은 비워 둘 수 없습니다.");
	}

}
