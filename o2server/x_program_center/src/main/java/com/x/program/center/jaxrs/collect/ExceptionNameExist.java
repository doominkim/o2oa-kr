package com.x.program.center.jaxrs.collect;

import java.util.Objects;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNameExist extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionNameExist(String name) {
		super("사용자: {}은(는) 이미 등록되어 있습니다.",Objects.toString(name) );
	}
}
