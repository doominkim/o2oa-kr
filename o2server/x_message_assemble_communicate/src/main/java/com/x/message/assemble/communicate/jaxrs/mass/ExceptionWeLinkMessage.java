package com.x.message.assemble.communicate.jaxrs.mass;

import com.x.base.core.project.exception.PromptException;

class ExceptionWeLinkMessage extends PromptException {


	private static final long serialVersionUID = 2459165718535980205L;

	ExceptionWeLinkMessage(String retCode, String retMessage) {
		super("Huawei WeLink 메시지 전송에 실패했습니다. 오류 코드: {}, 오류 메시지: {}.", retCode, retMessage);
	}
}
