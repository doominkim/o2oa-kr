package com.x.organization.assemble.authentication.jaxrs.qiyeweixin;

import com.x.base.core.project.exception.PromptException;

class ExceptionCodeEmpty extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionCodeEmpty() {
		super("WeCom 로그인 code는 비워 둘 수 없습니다.");
	}
}
