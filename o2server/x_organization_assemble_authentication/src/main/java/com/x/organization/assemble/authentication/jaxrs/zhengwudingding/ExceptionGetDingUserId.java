package com.x.organization.assemble.authentication.jaxrs.zhengwudingding;

import com.x.base.core.project.exception.PromptException;

class ExceptionGetDingUserId extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionGetDingUserId(Integer errcode, String errmsg) {
		super("DingTalk userId 조회 오류, 코드 {}, 메시지: {}.", errcode, errmsg);
	}
}
