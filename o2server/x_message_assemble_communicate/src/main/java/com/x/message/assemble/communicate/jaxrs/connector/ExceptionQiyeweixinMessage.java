package com.x.message.assemble.communicate.jaxrs.connector;

import com.x.base.core.project.exception.PromptException;

class ExceptionQiyeweixinMessage extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionQiyeweixinMessage(Integer retCode, String retMessage) {
		super("WeCom 메시지 전송에 실패했습니다. 오류 코드: {}, 오류 메시지: {}.", retCode, retMessage);
	}
}
