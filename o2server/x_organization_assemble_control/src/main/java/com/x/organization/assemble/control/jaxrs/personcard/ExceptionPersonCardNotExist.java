package com.x.organization.assemble.control.jaxrs.personcard;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPersonCardNotExist extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionPersonCardNotExist(String flag) {
		super("개인 명함: {}이(가) 없습니다.", flag);
	}
}
