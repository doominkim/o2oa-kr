package com.x.organization.assemble.authentication.jaxrs.oauth;

import com.x.base.core.project.exception.PromptException;

class ExceptionGetInitialManagerToken extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionGetInitialManagerToken() {
		super("oauth로 초기 관리자의 token을 가져올 수 없습니다.");
	}
}
