package com.x.processplatform.assemble.surface.jaxrs.data;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyData extends LanguagePromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionEmptyData() {
		super("수정할 데이터는 비워 둘 수 없습니다.");
	}
}
