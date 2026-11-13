package com.x.organization.assemble.authentication.jaxrs.mpweixin;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonNotExist extends PromptException {

	private static final long serialVersionUID = -1958450107076413280L;

	ExceptionPersonNotExist() {
		super("사용자가 없거나 아직 WeChat 공식 계정과 연결되지 않았습니다");
	}
}
