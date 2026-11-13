package com.x.cms.assemble.control.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionAppInfoExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionAppInfoExist(String flag) {
		super("앱: {}이(가) 이미 있습니다.", flag);
	}
}
