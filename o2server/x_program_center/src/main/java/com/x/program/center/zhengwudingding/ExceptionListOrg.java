package com.x.program.center.zhengwudingding;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionListOrg extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionListOrg(Integer retCode, String retMessage) {
		super("정부용 DingTalk에서 하위 조직을 가져오지 못했습니다, 오류 코드: {}, 오류 메시지: {}.", retCode, retMessage);
	}
}
