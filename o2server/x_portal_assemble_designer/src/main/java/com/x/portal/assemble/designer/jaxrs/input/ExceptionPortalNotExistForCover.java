package com.x.portal.assemble.designer.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionPortalNotExistForCover extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionPortalNotExistForCover(String id, String name, String alias) {
		super("덮어쓸 포털을 찾을 수 없습니다, id: {}, name: {}, alias: {}.", id, name, alias);
	}
}
