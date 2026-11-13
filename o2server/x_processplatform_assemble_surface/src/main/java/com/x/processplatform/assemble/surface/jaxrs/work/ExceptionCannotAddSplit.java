package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionCannotAddSplit extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionCannotAddSplit(String id) {
		super("문서: {}, 현재 상태에서는 분기를 추가할 수 없습니다.", id);
	}
}
