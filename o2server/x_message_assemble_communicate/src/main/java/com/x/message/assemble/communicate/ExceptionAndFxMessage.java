package com.x.message.assemble.communicate;

import com.x.base.core.project.exception.PromptException;

class ExceptionAndFxMessage extends PromptException {

	private static final long serialVersionUID = -4676359299794527980L;

	ExceptionAndFxMessage(Integer retCode, String retMessage) {
		super("모바일 오피스 메시지 전송에 실패했습니다. 오류 코드: {}, 오류 메시지: {}.", retCode, retMessage);
	}
}
