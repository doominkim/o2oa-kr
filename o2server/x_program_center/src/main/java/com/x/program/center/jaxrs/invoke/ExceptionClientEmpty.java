package com.x.program.center.jaxrs.invoke;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionClientEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionClientEmpty() {
		super("sso 설정 client는 비워 둘 수 없습니다.");
	}
}
