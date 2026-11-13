package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDisableCollect extends LanguagePromptException {

	private static final long serialVersionUID = 6351023802034208595L;

	ExceptionDisableCollect() {
		super("SMS 전송에 실패했습니다. 관리자에게 O2 클라우드 연결 설정을 요청하세요.");
	}
}
