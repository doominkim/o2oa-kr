package com.x.portal.assemble.designer.jaxrs.script;

import com.x.base.core.project.exception.LanguagePromptException;

class PortalInvisibleException extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	PortalInvisibleException(String person, String name, String id) {
		super("사용자: {}에게 이 포털 name: {} id: {}에 접근할 권한이 없습니다.", person, name, id);
	}
}
