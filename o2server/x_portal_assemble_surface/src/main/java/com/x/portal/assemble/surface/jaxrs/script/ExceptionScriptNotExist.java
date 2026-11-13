package com.x.portal.assemble.surface.jaxrs.script;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionScriptNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -4908883340253465376L;

	ExceptionScriptNotExist(String id) {
		super("지정한 스크립트가 없습니다: {}.", id);
	}

}
