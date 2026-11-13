package com.x.message.assemble.communicate.jaxrs.mass;

import com.x.base.core.project.exception.PromptException;

class ExceptionDisable extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDisable() {
		super("Huawei WeLink, WeCom, DingTalk, 정부용 DingTalk이 모두 사용 설정되어 있지 않습니다.");
	}
}
