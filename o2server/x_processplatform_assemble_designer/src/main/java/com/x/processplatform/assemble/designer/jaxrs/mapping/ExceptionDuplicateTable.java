package com.x.processplatform.assemble.designer.jaxrs.mapping;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDuplicateTable extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionDuplicateTable() {
		super("열람 대기 매핑이 이미 있습니다.");
	}

}
