package com.x.organization.assemble.authentication.jaxrs.zhengwudingding;

import com.x.base.core.project.exception.PromptException;

class ExceptionGetUserIdWithDingUserId extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionGetUserIdWithDingUserId(Integer errcode, String errmsg) {
		super("DingTalk userId를 정부용 DingTalk userId로 변환하는 중 오류, 코드 {}, 메시지: {}.", errcode, errmsg);
	}
}
