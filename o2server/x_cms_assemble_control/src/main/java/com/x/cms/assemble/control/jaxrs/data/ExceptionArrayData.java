package com.x.cms.assemble.control.jaxrs.data;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionArrayData extends LanguagePromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionArrayData( ) {
		super("수정할 데이터는 배열일 수 없습니다.");
	}
}
