package com.x.cms.assemble.control.jaxrs.data;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNotJsonObject extends LanguagePromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionNotJsonObject() {
		super("수정할 데이터는 jsonObject여야 합니다.");
	}
}
