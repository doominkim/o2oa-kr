package com.x.cms.assemble.control.jaxrs.data;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPrimitiveData extends LanguagePromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionPrimitiveData() {
		super("수정할 데이터는 Primitive일 수 없습니다.");
	}
}
