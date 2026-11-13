package com.x.processplatform.assemble.designer.jaxrs.process;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionProcessEnabled extends LanguagePromptException {

	private static final long serialVersionUID = 3768001625178470667L;

	ExceptionProcessEnabled(String flag) {
		super("프로세스: {}에 여러 버전이 있고 현재 버전이 사용 중입니다. 다른 버전을 사용 설정한 뒤 삭제하세요.", flag);
	}
}
