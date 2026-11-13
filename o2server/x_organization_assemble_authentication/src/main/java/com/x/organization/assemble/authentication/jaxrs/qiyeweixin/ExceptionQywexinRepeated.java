package com.x.organization.assemble.authentication.jaxrs.qiyeweixin;

import com.x.base.core.project.exception.PromptException;

class ExceptionQywexinRepeated extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionQywexinRepeated(String userId) {
		super("중복된 WeCom userid가 조회되었습니다. {} " ,userId);
	}
}
