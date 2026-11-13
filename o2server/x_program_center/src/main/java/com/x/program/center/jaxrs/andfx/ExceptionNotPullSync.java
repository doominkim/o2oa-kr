package com.x.program.center.jaxrs.andfx;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNotPullSync extends LanguagePromptException {

	private static final long serialVersionUID = -5403519407965886848L;

	ExceptionNotPullSync() {
		super("모바일 오피스에서 가져오는 동기화가 사용 설정되어 있지 않습니다.");
	}
}
