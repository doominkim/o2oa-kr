package com.x.program.center.schedule;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionZhengwuDingdingRegistCallback extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionZhengwuDingdingRegistCallback(Integer code, String message) {
		super("정부용 DingTalk 콜백 주소 등록 실패, 오류 코드: {}, 오류 메시지: {}.", code, message);
	}
}