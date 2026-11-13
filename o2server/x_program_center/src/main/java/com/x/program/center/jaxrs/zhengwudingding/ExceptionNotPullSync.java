package com.x.program.center.jaxrs.zhengwudingding;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNotPullSync extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionNotPullSync() {
		super("정부용 DingTalk에서 가져오는 동기화가 사용 설정되어 있지 않습니다.");
	}
}
