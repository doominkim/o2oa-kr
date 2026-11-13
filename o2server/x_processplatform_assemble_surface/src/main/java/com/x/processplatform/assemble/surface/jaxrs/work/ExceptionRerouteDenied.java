package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionRerouteDenied extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionRerouteDenied(String person, String title, String activityName) {
		super("사용자 {}의 문서 {}을(를) 단계: {}(으)로 이동하는 요청이 거부되었습니다.", person, title, activityName);
	}
}
