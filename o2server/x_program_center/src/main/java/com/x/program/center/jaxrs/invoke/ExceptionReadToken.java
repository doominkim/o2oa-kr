package com.x.program.center.jaxrs.invoke;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionReadToken extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionReadToken(String client, String token) {
		super("sso 토큰을 읽을 수 없습니다, 클라이언트: {}, 토큰: {}.", client, token);
	}
}
