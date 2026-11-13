package com.x.processplatform.assemble.designer.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionApplicationExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionApplicationExist(String flag) {
		super("앱: {}이(가) 이미 있습니다.", flag);
	}
}
