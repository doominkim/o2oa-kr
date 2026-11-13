package com.x.organization.assemble.control.jaxrs.group;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionGroupNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionGroupNotExist(String flag) {
		super("그룹: {}이(가) 없습니다.", flag);
	}
}
