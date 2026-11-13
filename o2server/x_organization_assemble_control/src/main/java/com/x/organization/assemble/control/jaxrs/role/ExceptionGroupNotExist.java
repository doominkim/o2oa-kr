package com.x.organization.assemble.control.jaxrs.role;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionGroupNotExist extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionGroupNotExist(String flag) {
		super("그룹: {}이(가) 없습니다.", flag);
	}
}
