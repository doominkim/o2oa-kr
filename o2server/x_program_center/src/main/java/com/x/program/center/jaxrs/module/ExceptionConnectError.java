package com.x.program.center.jaxrs.module;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionConnectError extends LanguagePromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionConnectError() {
		super("외부 등록 서버에 연결할 수 없습니다. 서버가 Internet에 정상적으로 연결되는지, DNS 확인이 정상인지 점검하세요.");
	}
}
