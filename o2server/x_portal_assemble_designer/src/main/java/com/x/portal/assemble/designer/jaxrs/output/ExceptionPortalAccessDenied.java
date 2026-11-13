package com.x.portal.assemble.designer.jaxrs.output;

import com.x.base.core.project.exception.PromptException;

class ExceptionPortalAccessDenied extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionPortalAccessDenied(String person, String name, String id) {
		super("사용자: {}의 포털 name: {} id: {} 접근이 거부되었습니다.", person, name, id);
	}

}
