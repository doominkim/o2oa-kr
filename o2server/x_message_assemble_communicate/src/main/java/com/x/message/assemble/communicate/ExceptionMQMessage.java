package com.x.message.assemble.communicate;

import com.x.base.core.project.exception.PromptException;

class ExceptionMQMessage extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionMQMessage(Integer retCode, String retMessage) {
		super("메시지 큐 전송에 실패했습니다. 오류 코드: {}, 오류 메시지: {}.", retCode, retMessage);
	}
}
