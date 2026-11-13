package com.x.cms.assemble.control.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionFlagEmpty extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionFlagEmpty() {
		super("식별자가 비어 있습니다.");
	}
}
