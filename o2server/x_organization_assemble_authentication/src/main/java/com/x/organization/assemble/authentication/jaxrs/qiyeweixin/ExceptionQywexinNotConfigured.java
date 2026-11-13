package com.x.organization.assemble.authentication.jaxrs.qiyeweixin;

import com.x.base.core.project.exception.PromptException;

class ExceptionQywexinNotConfigured extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionQywexinNotConfigured() {
		super("WeCom 로그인이 설정되어 있지 않습니다.");
	}
}
