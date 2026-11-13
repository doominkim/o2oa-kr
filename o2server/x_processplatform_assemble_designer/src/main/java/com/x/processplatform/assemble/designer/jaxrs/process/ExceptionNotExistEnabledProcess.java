package com.x.processplatform.assemble.designer.jaxrs.process;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNotExistEnabledProcess extends LanguagePromptException {

	private static final long serialVersionUID = 7770778192986529177L;

	ExceptionNotExistEnabledProcess(String flag) {
		super("사용 중인 프로세스가 없습니다: {}", flag);
	}
}
