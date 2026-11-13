package com.x.program.center.jaxrs.input;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAppInfoNotFound extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionAppInfoNotFound(String name, String id, String alias) {
		super("앱 이름 {}, id: {}, 별칭: {}을(를) 찾을 수 없습니다.", name, id, alias);
	}
}
