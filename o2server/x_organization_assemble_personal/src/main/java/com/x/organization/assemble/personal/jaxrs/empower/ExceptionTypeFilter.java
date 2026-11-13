package com.x.organization.assemble.personal.jaxrs.empower;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionTypeFilter extends LanguagePromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	ExceptionTypeFilter(String identity, String process, String filter) {
		super("소속 {}의 지정 필터 조건 권한 위임에 오류가 있습니다.", identity, process, filter);
	}

}
