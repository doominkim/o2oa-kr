package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidName extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionInvalidName(String name) {
		super("사용자 이름 오류: 비워 둘 수 없고 특수 문자를 사용할 수 없습니다: {}.", name);
	}
}
