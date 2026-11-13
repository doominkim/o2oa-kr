package com.x.program.center.schedule;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionZhengwuDingdingDeleteCallback extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionZhengwuDingdingDeleteCallback(Integer code, String message) {
		super("정부용 DingTalk app access token을 가져오지 못했습니다. 오류 코드: {}, 오류 메시지: {}.", code, message);
	}
}