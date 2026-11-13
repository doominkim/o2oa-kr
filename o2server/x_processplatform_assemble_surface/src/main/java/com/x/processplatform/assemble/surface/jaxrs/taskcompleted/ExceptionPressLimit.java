package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPressLimit extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionPressLimit(Integer interval, Integer minutes) {
		super("{}분 내 {}회 실행 제한에 걸렸습니다.", interval, minutes);
	}
}
