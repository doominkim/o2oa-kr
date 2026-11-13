package com.x.portal.assemble.designer.jaxrs.portal;

import com.x.base.core.project.exception.LanguagePromptException;

class PortalInsufficientPermissionException extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	PortalInsufficientPermissionException(String person, String name, String id) {
		super("사용자: {}에게 포털: {}, id: {}을(를) 다룰 권한이 부족합니다.", person, name, id);
	}
}
