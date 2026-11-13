package com.x.program.center.jaxrs.dingding;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionRegisterCallbackMessage extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionRegisterCallbackMessage(Integer retCode, String retMessage) {
		super("DingTalk 메시지 전송에 실패했습니다. 오류 코드: {}, 오류 메시지: {}.", retCode, retMessage);
	}
}
