package com.x.program.center.zhengwudingding;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionListUser extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionListUser(Integer retCode, String retMessage) {
		super("정부용 DingTalk에서 조직 구성원을 가져오지 못했습니다, 오류 코드: {}, 오류 메시지: {}.", retCode, retMessage);
	}
}
