package com.x.message.assemble.communicate.jaxrs.mass;

import com.x.base.core.project.exception.PromptException;

class ExceptionZhengwuDingdingMessage extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionZhengwuDingdingMessage(Integer retCode, String retMessage) {
		super("정부용 DingTalk 메시지 전송에 실패했습니다. 오류 코드: {}, 오류 메시지: {}.", retCode, retMessage);
	}
}
