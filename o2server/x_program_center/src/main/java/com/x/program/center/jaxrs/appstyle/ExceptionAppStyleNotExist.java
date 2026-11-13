package com.x.program.center.jaxrs.appstyle;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAppStyleNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionAppStyleNotExist(String str) {
		super("모바일 클라이언트 스타일: {}이(가) 없습니다.", str);
	}
}
