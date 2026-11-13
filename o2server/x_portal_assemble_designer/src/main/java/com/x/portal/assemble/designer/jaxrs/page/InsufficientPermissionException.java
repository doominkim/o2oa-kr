package com.x.portal.assemble.designer.jaxrs.page;

import com.x.base.core.project.exception.LanguagePromptException;

class InsufficientPermissionException extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	InsufficientPermissionException(String person) {
		super("사용자: {}에게 이 작업을 수행할 권한이 없습니다.", person);
	}
}
