package com.x.program.center.jaxrs.zhengwudingding;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionGetRegistCallback extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionGetRegistCallback(Integer retCode, String retMessage) {
		super("정부용 DingTalk에 등록된 콜백 주소 정보를 가져오지 못했습니다, 오류 코드: {}, 오류 메시지: {}.", retCode, retMessage);
	}
}
