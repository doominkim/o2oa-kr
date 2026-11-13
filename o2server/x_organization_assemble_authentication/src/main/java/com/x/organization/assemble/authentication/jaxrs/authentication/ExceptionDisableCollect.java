package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDisableCollect extends LanguagePromptException {

	private static final long serialVersionUID = 6351023802034208595L;

	public static String defaultMessage = "시스템에서 노드 연결을 사용하지 않습니다.";

	ExceptionDisableCollect() {
		super(defaultMessage);
	}
}
