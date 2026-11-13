package com.x.processplatform.assemble.surface.jaxrs.serialnumber;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionSerialNumberNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionSerialNumberNotExist(String flag) {
		super("없는 일련번호: {}.", flag);
	}
}
