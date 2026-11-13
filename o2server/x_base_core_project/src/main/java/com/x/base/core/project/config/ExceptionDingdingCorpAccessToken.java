package com.x.base.core.project.config;

import com.x.base.core.project.exception.PromptException;

class ExceptionDingdingCorpAccessToken extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionDingdingCorpAccessToken(Integer code, String message) {
		super("DingTalk access token을 가져오지 못했습니다. 오류 코드: {}, 오류 메시지: {}.", code, message);
	}
}