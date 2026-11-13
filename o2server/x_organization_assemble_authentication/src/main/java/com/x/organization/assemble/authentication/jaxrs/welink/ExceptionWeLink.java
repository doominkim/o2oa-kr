package com.x.organization.assemble.authentication.jaxrs.welink;

import com.x.base.core.project.exception.PromptException;

class ExceptionWeLink extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionWeLink(String errcode, String errmsg) {
		super("WeLink 오류 반환: errcode: {}, errmsg: {}.", errcode, errmsg);
	}
}
