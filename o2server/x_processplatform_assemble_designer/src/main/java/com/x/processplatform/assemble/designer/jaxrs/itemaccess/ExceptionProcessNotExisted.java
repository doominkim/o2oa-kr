package com.x.processplatform.assemble.designer.jaxrs.itemaccess;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionProcessNotExisted extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionProcessNotExisted(String flag) {
		super("지정한 결재가 없습니다: {}.", flag);
	}
}
