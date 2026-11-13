package com.x.query.assemble.designer.jaxrs.output;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionRevealNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionRevealNotExist(String flag) {
		super("표시: {}이(가) 없습니다.", flag);
	}
}
