package com.x.portal.assemble.designer.jaxrs.portal;

import com.x.base.core.project.exception.LanguagePromptException;

class PortalNotExistedException extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	PortalNotExistedException(String id) {
		super("지정한 포털이 없습니다: {}.", id);
	}
}
