package com.x.portal.assemble.designer.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionPortalNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionPortalNotExist(String flag) {
		super("포털: {}이(가) 없습니다.", flag);
	}

	ExceptionPortalNotExist(String id, String name, String alias) {
		super("포털 id: {}, name: {}, alias: {}이(가) 없습니다.", id, name, alias);
	}
}
