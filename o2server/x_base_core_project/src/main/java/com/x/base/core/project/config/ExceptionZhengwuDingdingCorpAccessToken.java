package com.x.base.core.project.config;

import com.x.base.core.project.exception.PromptException;

class ExceptionZhengwuDingdingCorpAccessToken extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionZhengwuDingdingCorpAccessToken(Integer errcode, String errmsg) {
		super("정부용 DingTalk JsapiTicket을 가져오지 못했습니다. errcode: {}, errmsg: {}.", errcode, errmsg);
	}
}