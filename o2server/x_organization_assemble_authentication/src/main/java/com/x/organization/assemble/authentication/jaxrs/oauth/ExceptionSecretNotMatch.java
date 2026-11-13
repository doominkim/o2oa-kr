package com.x.organization.assemble.authentication.jaxrs.oauth;

import com.x.base.core.project.exception.PromptException;

class ExceptionSecretNotMatch extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionSecretNotMatch(String client_id, String client_secret) {
		super("클라이언트 식별자: {}와 키: {}가 일치하지 않습니다.", client_id, client_secret);
	}
}
