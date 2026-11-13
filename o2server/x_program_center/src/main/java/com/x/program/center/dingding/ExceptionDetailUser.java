package com.x.program.center.dingding;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDetailUser extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDetailUser(Integer retCode, String retMessage) {
		super("DingTalk에서 사용자 정보를 가져오지 못했습니다, 오류 코드: {}, 오류 메시지: {}.", retCode, retMessage);
	}
}
