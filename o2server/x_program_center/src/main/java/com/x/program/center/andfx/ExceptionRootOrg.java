package com.x.program.center.andfx;

import com.x.base.core.project.exception.PromptException;

class ExceptionRootOrg extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionRootOrg(Integer retCode, String retMessage) {
		super("모바일 오피스에서 최상위 조직을 가져오지 못했습니다, 오류 코드: {}, 오류 메시지: {}.", retCode, retMessage);
	}
}
